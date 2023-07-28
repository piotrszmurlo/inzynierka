import base64
from src.models import RemoteDataFile
import numpy as np


def parse_results_file(remote_data_file: RemoteDataFile):
    algorithm_name, function_number, dimension = remote_data_file.name.split(".", 1)[0].split("_")
    contents = base64.b64decode(remote_data_file.content).decode('utf-8')
    rows = contents.split("\n")
    results_matrix = np.zeros((17, 30))
    for i, row in enumerate(rows):
        results_matrix = np.insert(results_matrix, i, row.split(" "), axis=0)
    print(results_matrix)
    return algorithm_name, function_number, dimension, contents
