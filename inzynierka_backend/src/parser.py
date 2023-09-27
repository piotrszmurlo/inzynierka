import base64

import numpy as np

from src.models import RemoteDataFile, LocalFile, ParseError
import python_extensions as extensions

ALLOWED_EXTENSIONS = ("txt", "dat")
FINAL_ERROR_INDEX = 15
FINAL_FES_INDEX = 16
FUNCTIONS_COUNT = 5
DIMENSION_10 = 10
DIMENSION_20 = 20


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


def get_final_error_and_fesV2(data_file: LocalFile):
    rows = data_file.contents.split("\n")
    evaluations = rows[FINAL_FES_INDEX].split()
    results = []
    for i, final_error in enumerate(rows[FINAL_ERROR_INDEX].split()):
        results.append(extensions.FunctionAlgorithmTrial(data_file.algorithm_name, data_file.function_number, i, float(final_error), int(evaluations[i].split(".")[0])))
    return results


# results[function_number - 1]
def get_final_error_and_fes_for_filesV2(data_files: list[LocalFile]):
    results = [[] for _ in range(FUNCTIONS_COUNT)]
    for data_file in data_files:
        results[data_file.function_number - 1] += get_final_error_and_fesV2(data_file)
    return results


def get_updated_rankings(data_files: list[LocalFile]):
    averages = {dimension: {} for dimension in [DIMENSION_10, DIMENSION_20]}
    medians = {dimension: {} for dimension in [DIMENSION_10, DIMENSION_20]}
    cec2022 = {dimension: {} for dimension in [DIMENSION_10, DIMENSION_20]}
    for file in data_files:
        results_matrix = parse_file_to_numpy_array(file)
        averages[file.dimension][file.algorithm_name] = np.average(results_matrix[FINAL_ERROR_INDEX])
        medians[file.dimension][file.algorithm_name] = np.median(results_matrix[FINAL_ERROR_INDEX])
        cec2022[file.dimension][file.algorithm_name] = 0
    return medians, averages
