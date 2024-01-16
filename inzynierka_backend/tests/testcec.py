from src.dependencies.parser import FINAL_FES_INDEX, FINAL_ERROR_INDEX
from src.models.file import LocalFile


def cec2022(trial_count: int, dimension: int, input: list):
    number_of_functions = len(input)
    output = []
    scores = {}
    for trial in input:
        sorted_trial = sorted(trial, key= lambda x: x[3], reverse=True)
        equal_values_count = 1

        for i in range(len(sorted_trial)):
            if i != len(sorted_trial) - 1 and sorted_trial[i] == sorted_trial[i + 1]:
                equal_values_count += 1
            else:
                score = (2 * i + 3 - equal_values_count) / float(2)
                for k in range(equal_values_count):
                    if sorted_trial[i-k][0] in scores.keys():
                        scores[sorted_trial[i - k][0]] += score
                    else:
                        scores[sorted_trial[i - k][0]] = score
                equal_values_count = 1
    correctionTerm = trial_count * (trial_count - 1) / 2 * number_of_functions
    for name, score in scores.items():
        output.append(
            (dimension, name, None, score - correctionTerm)
        )
    return output

def get_final_error_and_evaluation_number_for_filestest(data_files: list[LocalFile], function_count: int):
    results = []
    for _ in range(function_count):
        results.append([])
    for data_file in data_files:
        results[data_file.function_number - 1].extend(get_final_error_and_evaluations_numbertest(data_file))
    return results

def get_final_error_and_evaluations_numbertest(data_file: LocalFile):
    rows = data_file.contents.split("\n")
    evaluations = rows[FINAL_FES_INDEX].split()
    results = []
    for i, final_error in enumerate(rows[FINAL_ERROR_INDEX].split()):
        results.append(
            (
                data_file.algorithm_name,
                data_file.function_number,
                i,
                float(final_error),
                int(evaluations[i].split(".")[0])
            )
        )
    return results