import numpy as np
import python_extensions as extensions
from scipy.stats import wilcoxon

from src import get_final_error_and_evaluation_number_for_files_grouped_by_algorithm, FileService, \
    StatisticRankingEntry, get_final_error_and_evaluations_number_array, ALL_DIMENSIONS, \
    get_final_error_and_evaluation_number_for_files, TRIALS_COUNT, get_all_errors_and_evaluations_number, \
    get_all_errors_and_evaluations_numbers_for_files
from src.mappers import map_statistic_ranking_entries_to_pydantic_model, map_score_ranking_entries_to_pydantic_model, \
    map_revisited_ranking_entries_to_pydantic_model, map_ecdf_entries_to_pydantic_model
from src.models import PairTestEntry

DIM2BUDGET = {
    10: 200000,
    20: 1000000
}
REVISITED_WEIGHT = 0.01


class Rankings:

    def __init__(
            self,
            file_service: FileService,
            thresholds=np.logspace(3, -8, num=51),
            revisited_ranking_weight=0.01,
            dimensionBudget: dict[int, int] = {10: 200000, 20: 1000000}
    ):
        self.dimensionBudget = dimensionBudget
        self._file_service = file_service
        self._thresholds = thresholds
        self._revisited_ranking_weight = revisited_ranking_weight
        self._statistics_ranking_data = None
        self._cec2022_ranking_scores = None
        self._friedman_ranking_scores = None
        self._revisited_ranking_data = None

    def invalidate_cache(self):
        self._statistics_ranking_data = None
        self._cec2022_ranking_scores = None
        self._friedman_ranking_scores = None
        self._revisited_ranking_data = None

    def get_statistics_ranking_data(self) -> list[StatisticRankingEntry]:
        if self._statistics_ranking_data is None:
            ranking_entries = extensions.calculate_statistics_entries(
                get_final_error_and_evaluation_number_for_files_grouped_by_algorithm(
                    self._file_service.get_files()
                )
            )
            self._statistics_ranking_data = map_statistic_ranking_entries_to_pydantic_model(
                ranking_entries
            )
        return self._statistics_ranking_data

    def get_cec2022_ranking_scores(self):
        if self._cec2022_ranking_scores is None:
            self._cec2022_ranking_scores = []
            for dimension in ALL_DIMENSIONS:
                errors = get_final_error_and_evaluation_number_for_files(
                    self._file_service.get_files_for_dimension(dimension)
                )
                score_entries = extensions.calculate_cec2022_scores(
                    TRIALS_COUNT, dimension, errors
                )
                self._cec2022_ranking_scores.extend(
                    map_score_ranking_entries_to_pydantic_model(score_entries))
        return self._cec2022_ranking_scores

    def get_friedman_ranking_scores(self):
        if self._friedman_ranking_scores is None:
            self._friedman_ranking_scores = []
            for dimension in ALL_DIMENSIONS:
                errors = get_final_error_and_evaluation_number_for_files(
                    self._file_service.get_files_for_dimension(dimension)
                )
                score_entries = extensions.calculate_friedman_scores(
                    TRIALS_COUNT, dimension, errors
                )
                self._friedman_ranking_scores.extend(
                    map_score_ranking_entries_to_pydantic_model(score_entries))
        return self._friedman_ranking_scores

    def get_revisited_ranking_entries(self):
        if self._revisited_ranking_data is None:
            ranking_entries = extensions.calculate_revisited_ranking(
                get_final_error_and_evaluation_number_for_files_grouped_by_algorithm(
                    self._file_service.get_files()
                ),
                self._thresholds,
                self.dimensionBudget,
                self._revisited_ranking_weight
            )
            self._revisited_ranking_data = map_revisited_ranking_entries_to_pydantic_model(
                ranking_entries)
        return self._revisited_ranking_data

    def get_ecdf_data(self):
        _ecdf_data = get_all_errors_and_evaluations_numbers_for_files(
            self._file_service.get_files()
        )
        res = extensions.calculate_ecdf_data(_ecdf_data, self._thresholds, self.dimensionBudget)

        return map_ecdf_entries_to_pydantic_model(res)

    def get_wilcoxon_test(self, first_algorithm: str, second_algorithm: str, dimension: int):
        results = []
        function_numbers = self._file_service.get_function_numbers()
        for function_number in function_numbers:
            results.append(
                self._get_wilcoxon_test_for_function(first_algorithm, second_algorithm, dimension, function_number)
            )
        return results

    def _get_wilcoxon_test_for_function(self, first_algorithm_name: str, second_algorithm_name: str, dimension: int,
                                        function_number: int):
        first_errors = get_final_error_and_evaluations_number_array(
            self._file_service.get_file(
                algorithm_name=first_algorithm_name,
                dimension=dimension,
                function_number=function_number
            )
        )
        second_errors = get_final_error_and_evaluations_number_array(
            self._file_service.get_file(
                algorithm_name=second_algorithm_name,
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
                    return PairTestEntry(
                        function_number=function_number,
                        winner=first_algorithm_name
                    )
                else:
                    return PairTestEntry(
                        function_number=function_number,
                        winner=second_algorithm_name
                    )

            else:
                return PairTestEntry(
                    function_number=function_number,
                    winner=None
                )
        except ValueError as e:
            if "zero for all elements" in str(e):
                return PairTestEntry(
                    function_number=function_number,
                    winner=None
                )
