import base64
from collections import defaultdict

import python_extensions as extensions

from src.models.file import LocalFile
from src.models.parse_error import ParseError

ALLOWED_EXTENSIONS = ("txt", "dat")
FINAL_ERROR_INDEX = 15
FINAL_FES_INDEX = 16
DIMENSION_10 = 10
DIMENSION_20 = 20
ALL_DIMENSIONS = [DIMENSION_10, DIMENSION_20]


def parse_remote_results_file(filename: str, contents: bytes, trial_count: int) -> tuple[str, int, int, str]:
    """
    Parses results file name and contents.

    Raises:
        ParseError - if the file cannot be parsed
        UnicodeDecodeError - if the file cannot be decoded with utf-8
        binascii.Error - is the padding for base64 is incorrect
    :param filename: name of the file in format: [ALGORITHM_NAME]_[FUNCTION_NUMBER]_[DIMENSION].[EXTENSION]
    :param contents: binary data encoded in base64
    :return: algorithm_name, function number, dimension and corrected contents extracted to a tuple
    """
    algorithm_name, function_number, dimension = parse_remote_filename(
        filename
    )
    DIM2BUDGET = {
        10: 200000,
        20: 1000000
    }

    raw_contents = base64.b64decode(contents).decode('utf-8')
    try:
        parsed_contents = extensions.parse_cec2022_results(raw_contents, filename, trial_count, DIM2BUDGET[dimension])
    except ValueError as e:
        raise ParseError(e.args[0])
    return algorithm_name, function_number, dimension, parsed_contents


def parse_remote_filename(filename: str) -> tuple[str, int, int]:
    """
    Parses file name and extracts necessary values to a tuple
    :param filename: file name in format: [ALGORITHM_NAME]_[FUNCTION_NUMBER]_[DIMENSION].[EXTENSION]
    :return: algorithm_name, function number and dimension extracted to a tuple
    """
    try:
        name, extension = filename.rsplit(".", 1)
        if extension not in ALLOWED_EXTENSIONS or "." in name:
            raise ParseError(f"Only {ALLOWED_EXTENSIONS} files allowed")
    except ValueError:
        raise ParseError(f"Only {ALLOWED_EXTENSIONS} files allowed")
    try:
        algorithm_name, function_number, dimension = name.rsplit("_", 2)
        if not function_number.isdigit() or not dimension.isdigit():
            raise ParseError(
                "File name must contain function number and dimension")
    except ValueError:
        raise ParseError(f"Unable to parse file name")
    return algorithm_name, int(function_number), int(dimension)


def get_final_error_and_evaluations_number(data_file: LocalFile) -> extensions.TrialsVector:
    """
    :param data_file: LocalFile with already preprocessed contents
    :return: TrialsVector containing final results from the file in form of Trial
    """
    rows = data_file.contents.split("\n")
    evaluations = rows[FINAL_FES_INDEX].split()
    results = extensions.TrialsVector()
    for i, final_error in enumerate(rows[FINAL_ERROR_INDEX].split()):
        results.append(
            extensions.Trial(
                data_file.algorithm_name,
                data_file.function_number,
                i,
                float(final_error),
                int(evaluations[i].split(".")[0])
            )
        )
    return results


def get_all_errors_and_evaluations_number(data_file: LocalFile) -> extensions.AllErrors:
    """
    :param data_file: LocalFile with already preprocessed contents
    :return: TrialsVector containing all results from the file in form of FullTrial
    """
    rows = data_file.contents.split("\n")
    errors = []
    for row in rows[:-2]: #
        errors.append([float(x) for x in row.split()])
    return extensions.AllErrors(
        data_file.algorithm_name,
        data_file.function_number,
        data_file.dimension,
        errors,
        [int(x.split(".")[0]) for x in rows[FINAL_FES_INDEX].split()]
    )


def get_all_errors_and_evaluations_numbers_for_files(data_files: list[LocalFile]):
    results = extensions.AllErrorsVector()
    for data_file in data_files:
        results.append(get_all_errors_and_evaluations_number(data_file))
    return results


def get_final_error_and_evaluations_number_array(data_file: LocalFile):
    """
    :param data_file: LocalFile with already preprocessed contents
    :return: list containing final error
    """
    rows = data_file.contents.split("\n")
    results = []
    for i, final_error in enumerate(rows[FINAL_ERROR_INDEX].split()):
        results.append(float(final_error))
    return results


# results[function_number - 1]
def get_final_error_and_evaluation_number_for_files(data_files: list[LocalFile], function_count: int) -> extensions.FunctionTrialsVector:
    """
    :param function_count: number of functions
    :param data_files: list of LocalFile(s) with already preprocessed contents
    :return: FunctionTrialsVector[TrialsVector[Trial]] with all final results provided
    """
    results = extensions.FunctionTrialsVector()
    for _ in range(function_count):
        results.append(extensions.TrialsVector())
    for data_file in data_files:
        results[data_file.function_number - 1].extend(get_final_error_and_evaluations_number(data_file))
    return results


# results[function_number - 1][algorithm_name]
def get_final_error_and_evaluation_number_for_files_grouped_by_algorithm(data_files: list[LocalFile], function_count: int):
    results = [defaultdict(dict) for _ in range(function_count)]
    for data_file in data_files:
        results[data_file.function_number - 1][
            data_file.dimension][data_file.algorithm_name] = get_final_error_and_evaluations_number(data_file)
    return extensions.BasicRankingInput(results)


def check_filenames_integrity(parsed_filenames: list[tuple], function_count: int):
    dimensions = []
    function_numbers = []
    filenames = []

    for filename, function_number, dimension in parsed_filenames:
        dimensions.append(dimension)
        function_numbers.append(function_number)
        filenames.append(filename)

    if len(set(filenames)) != 1:
        raise ParseError("Differing algorithm names in files")

    if set(dimensions) != set(ALL_DIMENSIONS):
        raise ParseError("Incompatible function dimensions")

    if any(function_number > function_count for function_number in function_numbers):
        raise ParseError("Incompatible function numbers")
