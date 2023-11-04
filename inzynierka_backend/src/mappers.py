import python_extensions as extensions

from src import StatisticRankingEntry, ScoreRankingEntry


def map_statistic_ranking_entries_to_pydantic_model(entries: list[extensions.StatisticsRankingEntry]) -> list[StatisticRankingEntry]:
    mapped_entries = [
        StatisticRankingEntry(
            dimension=entry.dimension,
            algorithm_name=entry.algorithm_name,
            function_number=entry.function_number,
            mean=round(entry.mean, 8),
            median=round(entry.median, 8),
            stdev=round(entry.stdev, 8),
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
            score=entry.score,
        ) for entry in entries
    ]
    return mapped_entries
