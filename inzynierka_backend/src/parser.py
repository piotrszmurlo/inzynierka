import base64

import numpy as np
from sqlalchemy.orm import Session

from src.dbaccess import get_files_for_dimension
from src.models import RemoteDataFile, LocalFile, ParseError
import python_extensions as extensions

ALLOWED_EXTENSIONS = ("txt", "dat")
FINAL_ERROR_INDEX = 15
FINAL_FES_INDEX = 16
FUNCTIONS_COUNT = 5
TRIALS_COUNT = 30
DIMENSION_10 = 10
DIMENSION_20 = 20
ALL_DIMENSIONS = [DIMENSION_10, DIMENSION_20]


def parse_remote_results_file(remote_data_file: RemoteDataFile) -> tuple[str, int, int, str]:
    algorithm_name, function_number, dimension = parse_remote_file_name(remote_data_file.name)
    raw_contents = base64.b64decode(remote_data_file.content).decode('utf-8')
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


def parse_file_to_numpy_array(data_file: LocalFile):
    rows = data_file.contents.split("\n")
    results_matrix = np.zeros((17, 30))
    for i, row in enumerate(rows):
        values = row.split()
        if values:
            results_matrix[i] = values
    return results_matrix


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
    for i in range(FUNCTIONS_COUNT):
        results.append(extensions.TrialsVector())
    for data_file in data_files:
        results[data_file.function_number - 1].extend(get_final_error_and_evaluations_number(data_file))

    return results


def get_updated_rankings(data_files: list[LocalFile], db: Session):
    averages = {dimension: {} for dimension in ALL_DIMENSIONS}
    medians = {dimension: {} for dimension in ALL_DIMENSIONS}
    cec2022 = {dimension: {} for dimension in ALL_DIMENSIONS}
    for file in data_files:
        results_matrix = parse_file_to_numpy_array(file)
        medians[file.dimension][file.algorithm_name] = np.median(results_matrix[FINAL_ERROR_INDEX])
    # for dimension in ALL_DIMENSIONS:
    for dimension in [DIMENSION_10]:
        results = get_final_error_and_evaluation_number_for_files(
            get_files_for_dimension(db, DIMENSION_10)
        )
        cec2022[dimension] = extensions.calculate_cec2022_score(FUNCTIONS_COUNT, TRIALS_COUNT, results)
        averages[dimension] = extensions.calculate_average(FUNCTIONS_COUNT, TRIALS_COUNT, results)
    return medians, averages, cec2022
