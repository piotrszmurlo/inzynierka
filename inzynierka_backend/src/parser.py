import base64
from src.models import RemoteDataFile, LocalFile
import numpy as np


def parse_results_file(remote_data_file: RemoteDataFile):
    algorithm_name, function_number, dimension = remote_data_file.name.rsplit(".", 1)[0].rsplit("_", 2)
    print(algorithm_name, function_number, dimension)
    raw_contents = base64.b64decode(remote_data_file.content).decode('utf-8')
    rows = raw_contents.split("\n")
    parsed_rows = []
    for i, row in enumerate(rows):
        if "," in raw_contents:
            values = row.split(',')
        else:
            values = row.split()
        if len(values) == 30:
            print(values)
            floats = list(map(lambda value: round(float(value), 8), values))
            parsed_row = " ".join(list(map(lambda x: str(x), floats)))
            parsed_rows.append(parsed_row)
    parsed_contents = "\n".join(parsed_rows)
    print(parsed_contents)
    return algorithm_name, function_number, dimension, parsed_contents


def update_rankings(data_files: list[LocalFile]):
    averages = {}
    medians = {}
    for file in data_files:
        rows = file.contents.split("\n")
        results_matrix = np.zeros((17, 30))
        for i, row in enumerate(rows):
            values = row.split()
            if values:
                results_matrix = np.insert(results_matrix, i, values, axis=0)
        averages[file.algorithm_name] = np.average(results_matrix[15])
        medians[file.algorithm_name] = np.median(results_matrix[15])
    return medians, averages
