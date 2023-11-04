import python_extensions as extensions
from scipy.stats import wilcoxon

from src import get_final_error_and_evaluation_number_for_files_grouped_by_algorithm, FileService, \
    StatisticRankingEntry, get_final_error_and_evaluations_number_array, ALL_DIMENSIONS, \
    get_final_error_and_evaluation_number_for_files, TRIALS_COUNT
from src.mappers import map_statistic_ranking_entries_to_pydantic_model, map_score_ranking_entries_to_pydantic_model
from src.models import PairTestEntry

class Rankings:

    def __init__(self, file_service: FileService):
        self._file_service = file_service
        self._statistics_ranking_data = None
        self._cec2022_ranking_scores = None
        self._friedman_ranking_scores = None

    def invalidate_cache(self):
        self._statistics_ranking_data = None
        self._cec2022_ranking_scores = None
        self._friedman_ranking_scores = None

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
                self._cec2022_ranking_scores.extend(map_score_ranking_entries_to_pydantic_model(score_entries))
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
                self._friedman_ranking_scores.extend(map_score_ranking_entries_to_pydantic_model(score_entries))
        return self._friedman_ranking_scores

    def get_wilcoxon_test(self, first_algorithm: str, second_algorithm: str, dimension: int):
        results = []
        function_numbers = self._file_service.get_function_numbers()
        for function_number in function_numbers:
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
                        results.append(
                            PairTestEntry(
                                function_number=function_number,
                                winner=first_algorithm
                            )
                        )
                    else:
                        results.append(
                            PairTestEntry(
                                function_number=function_number,
                                winner=second_algorithm
                            )
                        )

                else:
                    results.append(
                        PairTestEntry(
                            function_number=function_number,
                            winner=None
                        )
                    )
            except ValueError as e:
                if "zero for all elements" in str(e):
                    results.append(
                        PairTestEntry(
                            function_number=function_number,
                            winner=None
                        )
                    )
        return results
