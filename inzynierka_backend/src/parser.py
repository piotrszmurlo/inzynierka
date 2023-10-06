import base64

from fastapi import UploadFile
from sqlalchemy.orm import Session

from src.dbaccess import get_files_for_dimension
from src.models import LocalFile, ParseError
import python_extensions as extensions

ALLOWED_EXTENSIONS = ("txt", "dat")
FINAL_ERROR_INDEX = 15
FINAL_FES_INDEX = 16
FUNCTIONS_COUNT = 5
TRIALS_COUNT = 30
DIMENSION_10 = 10
DIMENSION_20 = 20
ALL_DIMENSIONS = [DIMENSION_10]
NUMBER_OF_STATISTICS = 4


def parse_remote_results_file(upload_file: UploadFile) -> tuple[str, int, int, str]:
    algorithm_name, function_number, dimension = parse_remote_file_name(upload_file.filename)
    raw_contents = base64.b64decode(upload_file.file.read()).decode('utf-8')
    parsed_contents = extensions.parse_results(raw_contents)
    return algorithm_name, function_number, dimension, parsed_contents


def parse_remote_file_name(file_name: str) -> tuple[str, int, int]:
    try:
        name, extension = file_name.rsplit(".", 1)
        if extension not in ALLOWED_EXTENSIONS or "." in name:
            raise ParseError(f"Only {ALLOWED_EXTENSIONS} files allowed")
    except ValueError:
        raise ParseError(f"Only {ALLOWED_EXTENSIONS} files allowed")
    try:
        algorithm_name, function_number, dimension = name.rsplit("_", 2)
        if not function_number.isdigit() or not dimension.isdigit():
            raise ParseError("File name must contain function number and dimension")
    except ValueError:
        raise ParseError(f"Unable to parse file name")
    return algorithm_name, function_number, dimension


def get_final_error_and_evaluations_number(data_file: LocalFile) -> extensions.TrialsVector:
    """
    :param data_file: LocalFile with already preprocessed contents
    :return: TrialsVector containing final results from the file in form of FunctionAlgorithmTrial
    """
    rows = data_file.contents.split("\n")
    evaluations = rows[FINAL_FES_INDEX].split()
    results = extensions.TrialsVector()
    for i, final_error in enumerate(rows[FINAL_ERROR_INDEX].split()):
        results.append(extensions.FunctionAlgorithmTrial(data_file.algorithm_name, data_file.function_number, i, float(final_error), int(evaluations[i].split(".")[0])))
    return results


# results[function_number - 1]
def get_final_error_and_evaluation_number_for_files(data_files: list[LocalFile]) -> extensions.FunctionTrialsVector:
    """
    :param data_files: list of LocalFile(s) with already preprocessed contents
    :return: FunctionTrialsVector[TrialsVector[FunctionAlgorithmTrial]] with all final results provided
    """
    results = extensions.FunctionTrialsVector()
    for _ in range(FUNCTIONS_COUNT):
        results.append(extensions.TrialsVector())
    for data_file in data_files:
        results[data_file.function_number - 1].extend(get_final_error_and_evaluations_number(data_file))
    return results


def get_updated_rankings(db: Session):
    averages, medians, cec2022, friedman = \
        ({dimension: {} for dimension in ALL_DIMENSIONS} for _ in range(NUMBER_OF_STATISTICS))
    for dimension in ALL_DIMENSIONS:
        results = get_final_error_and_evaluation_number_for_files(
            get_files_for_dimension(db, DIMENSION_10)
        )
        cec2022[dimension] = extensions.calculate_cec2022_score(FUNCTIONS_COUNT, TRIALS_COUNT, results)
        averages[dimension] = extensions.calculate_average(FUNCTIONS_COUNT, TRIALS_COUNT, results)
        medians[dimension] = extensions.calculate_median(results)
        # friedman[dimension] = {}
    return medians, averages, cec2022, friedman
