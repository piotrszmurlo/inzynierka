from typing import Optional

from pydantic import BaseModel
from sqlalchemy import Column, Integer, Text, UniqueConstraint
from sqlalchemy.orm import declarative_base
from sqlalchemy.dialects.mysql import VARCHAR

Base = declarative_base()


class ParseError(Exception):
    pass


class LocalFile(Base):
    __tablename__ = "files"
    id = Column(Integer, primary_key=True, index=True)
    contents = Column(Text)
    algorithm_name = Column(VARCHAR(255))
    dimension = Column(Integer)
    function_number = Column(Integer)
    UniqueConstraint("algorithm_name", "dimension", "function_number", name="uix_1")

    def __repr__(self):
        return f'LocalFile({self.id}, {self.algorithm_name}, {self.function_number}, {self.dimension})'


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


class ScoreRankingEntry(BaseModel):
    dimension: int
    algorithm_name: str
    score: float


class PairTestEntry(BaseModel):
    function_number: int
    winner: Optional[str]


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
