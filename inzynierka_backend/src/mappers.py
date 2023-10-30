import python_extensions as extensions

from src import StatisticRankingEntry


def map_statistic_ranking_entries_to_pydantic_model(entries: list[extensions.StatisticsRankingEntry]) -> list[StatisticRankingEntry]:
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
