import python_extensions as extensions
from scipy.stats import wilcoxon

from src import get_final_error_and_evaluation_number_for_files_grouped_by_algorithm, FileService, \
    StatisticRankingEntry, get_final_error_and_evaluations_number_array
from src.mappers import map_statistic_ranking_entries_to_pydantic_model


class Rankings:

    def __init__(self, file_service: FileService):
        self._file_service = file_service
        self._statistics_ranking_data = None

    def invalidate_cache(self):
        self._statistics_ranking_data = None

    def get_statistics_ranking_data(self) -> list[StatisticRankingEntry]:
        if self._statistics_ranking_data is None:
            ranking_entries = extensions.calculate_statisticsV2(
                get_final_error_and_evaluation_number_for_files_grouped_by_algorithm(
                    self._file_service.get_files()
                )
            )
            self._statistics_ranking_data = map_statistic_ranking_entries_to_pydantic_model(ranking_entries)
        return self._statistics_ranking_data

    def get_wilcoxon_test(self, first_algorithm: str, second_algorithm: str, dimension: int, function_number: int):
        first_errors = get_final_error_and_evaluations_number_array(
            self._file_service.get_file(
                algorithm_name=first_algorithm,
                dimension=dimension,
                function_number=function_number
            )
        )
        second_errors = get_final_error_and_evaluations_number_array(
            self._file_service.get_file(
                algorithm_name=second_algorithm,
                dimension=dimension,
                function_number=function_number
            )
        )
        diff = []
        for index in range(len(first_errors)):
            diff.append(first_errors[index] - second_errors[index])
        try:
            h0_p_value = wilcoxon(diff)[1]
            if h0_p_value < 0.05:
                h1_p_value = wilcoxon(diff, alternative="less")[1]
                if h1_p_value < 0.05:
                    return "-"
                return "+"

            else:
                return "="
        except ValueError as e:
            if "zero for all elements" in str(e):
                return "="


