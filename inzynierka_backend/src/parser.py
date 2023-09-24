import base64

import numpy as np

from src import models
from src.dbaccess import get_all_algorithm_names_for_dimension
from src.models import RemoteDataFile, LocalFile, ParseError
import python_extensions as extensions

ALLOWED_EXTENSIONS = ("txt", "dat")
LAST_ROW_INDEX = 15
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


def parse_file_to_matrix(data_file: LocalFile):
    rows = data_file.contents.split("\n")
    results_matrix = np.zeros((0, 30))
    for i, row in enumerate(rows):
        values = row.split()
        if values:
            results_matrix = np.insert(results_matrix, i, values, axis=0)
    return results_matrix


def update_rankings(data_files: list[LocalFile]):
    averages = {dimension: {} for dimension in [DIMENSION_10, DIMENSION_20]}
    medians = {dimension: {} for dimension in [DIMENSION_10, DIMENSION_20]}
    for file in data_files:
        results_matrix = parse_file_to_matrix(file)
        averages[file.dimension][file.algorithm_name] = np.average(results_matrix[LAST_ROW_INDEX])
        medians[file.dimension][file.algorithm_name] = np.median(results_matrix[LAST_ROW_INDEX])
    return medians, averages


def calculate_cec_ranking(db):
    all_algorithms = get_all_algorithm_names_for_dimension(db, 10)
    data_files = db.query(models.LocalFile).filter(models.LocalFile.dimension == 10).all()
    for algorithm in all_algorithms:
        algorithm_files = filter(lambda x: x.algorithm_name == algorithm, data_files)
        results = []
        for file in algorithm_files:
            print("GOWNOOOOOOO")
            # print(file)
            ooo = parse_file_to_matrix(file)
            print(ooo.shape)
            return
