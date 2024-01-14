from typing import Optional

from pydantic import BaseModel


class StatisticRankingEntry(BaseModel):
    dimension: int
    algorithm_name: str
    function_number: int
    mean: float
    median: float
    stdev: float
    max: float
    min: float
    number_of_evaluations: int


class PairTestEntry(BaseModel):
    function_number: int
    winner: Optional[str]


class ScoreRankingEntry(BaseModel):
    dimension: int
    algorithm_name: str
    function_number: Optional[int]
    score: float


class RevisitedRankingEntry(BaseModel):
    dimension: int
    algorithm_name: str
    function_number: int
    successful_trials_percentage: float
    thresholds_achieved_percentage: float
    budget_left_percentage: float
    score: float


class EcdfEntry(BaseModel):
    dimension: int
    algorithm_name: str
    function_number: int
    thresholds_achieved_fractions: list[float]
    function_evaluations: list[float]


