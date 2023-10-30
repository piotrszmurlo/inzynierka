import base64
from collections import defaultdict
from pprint import pprint

from sqlalchemy.orm import Session

from src.models import LocalFile, ParseError, StatisticRankingEntry
from src.dbaccess import get_files_for_dimension
import python_extensions as extensions

ALLOWED_EXTENSIONS = ("txt", "dat")
FINAL_ERROR_INDEX = 15
FINAL_FES_INDEX = 16
FUNCTIONS_COUNT = 5
TRIALS_COUNT = 30
DIMENSION_10 = 10
DIMENSION_20 = 20
ALL_DIMENSIONS = [DIMENSION_10, DIMENSION_20]
NUMBER_OF_STATISTICS = 4


def parse_remote_results_file(filename: str, contents: bytes) -> tuple[str, int, int, str]:
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
    algorithm_name, function_number, dimension = parse_remote_file_name(
        filename
    )
    raw_contents = base64.b64decode(contents).decode('utf-8')
    try:
        parsed_contents = extensions.parse_results(raw_contents)
    except ValueError as e:
        raise ParseError(e.args[0])
    return algorithm_name, function_number, dimension, parsed_contents


def parse_remote_file_name(filename: str) -> tuple[str, int, int]:
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
def get_final_error_and_evaluation_number_for_files(data_files: list[LocalFile]) -> extensions.FunctionTrialsVector:
    """
    :param data_files: list of LocalFile(s) with already preprocessed contents
    :return: FunctionTrialsVector[TrialsVector[Trial]] with all final results provided
    """
    results = extensions.FunctionTrialsVector()
    for _ in range(FUNCTIONS_COUNT):
        results.append(extensions.TrialsVector())
    for data_file in data_files:
        results[data_file.function_number - 1].extend(get_final_error_and_evaluations_number(data_file))
    return results


# results[function_number - 1][algorithm_name]
def get_final_error_and_evaluation_number_for_files_grouped_by_algorithm(data_files: list[LocalFile]):
    results = extensions.BasicRankingInput()
    listresult = [defaultdict(dict) for _ in range(FUNCTIONS_COUNT)]
    for data_file in data_files:
        listresult[data_file.function_number - 1][data_file.dimension][
            data_file.algorithm_name] = get_final_error_and_evaluations_number(data_file)
    results.extend(listresult)
    return results


def map_statistic_ranking_entries_to_pydantic_model(entries: list[extensions.StatisticsRankingEntry]) -> list[StatisticRankingEntry]:
    mapped_entries = [
        StatisticRankingEntry(
            dimension=entry.dimension,
            algorithm_name=entry.algorithm_name,
            function_number=entry.function_number,
            mean=entry.mean,
            median=entry.median,
            stdev=entry.stdev,
            max=entry.max,
            min=entry.min,
            number_of_evaluations=entry.number_of_evaluations
        ) for entry in entries
    ]
    return mapped_entries
