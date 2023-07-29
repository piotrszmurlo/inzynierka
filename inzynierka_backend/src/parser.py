import base64

from multipart.exceptions import ParseError
from sqlalchemy import and_

from src import models
from src.dbaccess import get_files_for_dimension, get_all_algorithm_names_for_dimension
from src.models import RemoteDataFile, LocalFile
import numpy as np


def parse_results_file(remote_data_file: RemoteDataFile):
    algorithm_name, function_number, dimension = remote_data_file.name.rsplit(".", 1)[0].rsplit("_", 2)
    print(algorithm_name, function_number, dimension)
    try:
        raw_contents = base64.b64decode(remote_data_file.content).decode('utf-8')
        rows = raw_contents.split("\n")
        parsed_rows = []
        for i, row in enumerate(rows):
            if "," in raw_contents:
                values = row.split(',')
            else:
                values = row.split()
            if len(values) == 30:
                floats = list(map(lambda value: round(float(value), 8), values))
                parsed_row = " ".join(list(map(lambda x: str(x), floats)))
                parsed_rows.append(parsed_row)
            parsed_contents = "\n".join(parsed_rows)
    except Exception:
        raise ParseError()
    return algorithm_name, function_number, dimension, parsed_contents


def parse_matrix(data_file: LocalFile):
    rows = data_file.contents.split("\n")
    results_matrix = np.zeros((0, 30))
    for i, row in enumerate(rows):
        values = row.split()
        if values:
            results_matrix = np.insert(results_matrix, i, values, axis=0)
            print(results_matrix.shape)
    return results_matrix


def update_rankings(data_files: list[LocalFile]):
    averages = {}
    medians = {}
    for file in data_files:
        results_matrix = parse_matrix(file)
        averages[file.algorithm_name] = np.average(results_matrix[15])
        medians[file.algorithm_name] = np.median(results_matrix[15])
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
            ooo = parse_matrix(file)
            print(ooo.shape)
            return