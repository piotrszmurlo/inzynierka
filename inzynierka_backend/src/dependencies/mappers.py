import python_extensions as extensions

from src.models.ranking_entry import StatisticRankingEntry, ScoreRankingEntry, EcdfEntry, RevisitedRankingEntry


def map_statistic_ranking_entries_to_pydantic_model(entries: list[extensions.StatisticsRankingEntry]) -> list[
    StatisticRankingEntry]:
    mapped_entries = [
        StatisticRankingEntry(
            dimension=entry.dimension,
            algorithm_name=entry.algorithm_name,
            function_number=entry.function_number,
            mean=entry.mean,
            median=entry.median,
            stdev=entry.stdev,
            max=entry.max,
            min=entry.min,
            number_of_evaluations=entry.number_of_evaluations
        ) for entry in entries
    ]
    return mapped_entries


def map_score_ranking_entries_to_pydantic_model(entries: list[extensions.ScoreRankingEntry]) -> list[ScoreRankingEntry]:
    mapped_entries = [
        ScoreRankingEntry(
            dimension=entry.dimension,
            algorithm_name=entry.algorithm_name,
            function_number=entry.function_number,
            score=entry.score
        ) for entry in entries
    ]
    return mapped_entries


def map_revisited_ranking_entries_to_pydantic_model(entries: list[extensions.RevisitedRankingEntry]) -> list[
    RevisitedRankingEntry]:
    mapped_entries = [
        RevisitedRankingEntry(
            dimension=entry.dimension,
            algorithm_name=entry.algorithm_name,
            function_number=entry.functionNumber,
            successful_trials_percentage=entry.successfulTrialsPercentage,
            thresholds_achieved_percentage=entry.thresholdsAchievedPercentage,
            budget_left_percentage=entry.budgetLeftPercentage,
            score=entry.score
        ) for entry in entries
    ]
    return mapped_entries


def map_ecdf_entries_to_pydantic_model(entries: list[extensions.EcdfEntry]) -> list[EcdfEntry]:
    mapped_entries = [
        EcdfEntry(
            dimension=entry.dimension,
            algorithm_name=entry.algorithm_name,
            function_number=entry.functionNumber,
            thresholds_achieved_fractions=entry.thresholds_achieved_fraction,
            function_evaluations=entry.function_evaluations
        ) for entry in entries
    ]
    return mapped_entries
