from typing import Optional, Union, List

from pydantic import BaseModel
from sqlalchemy import Column, Integer, Text, UniqueConstraint, Boolean, String, ForeignKey
from sqlalchemy.orm import relationship, mapped_column, Mapped
from sqlalchemy.orm import DeclarativeBase


class Base(DeclarativeBase):
    pass


class ParseError(Exception):
    pass


class LocalFile(Base):
    __tablename__ = "files"
    __table_args__ = (UniqueConstraint("algorithm_name", "dimension", "function_number", "benchmark_id", name="uix_1"),)
    id: Mapped[int] = mapped_column(Integer, primary_key=True, index=True)
    contents: Mapped[str] = mapped_column(Text)
    algorithm_name: Mapped[str] = mapped_column(String(255))
    dimension: Mapped[int]
    function_number: Mapped[int]
    benchmark_id: Mapped[int] = mapped_column(ForeignKey("benchmarks.id", ondelete="CASCADE"))
    benchmark: Mapped["Benchmark"] = relationship("Benchmark", back_populates="files")

    def __repr__(self):
        return f'LocalFile({self.id}, {self.algorithm_name}, {self.function_number}, {self.dimension})'


class UserTable(Base):
    __tablename__ = "users"
    id: Mapped[int] = mapped_column(Integer, primary_key=True, index=True)
    email: Mapped[str] = mapped_column(String(255), unique=True)
    password_hash: Mapped[str] = mapped_column(String(255))
    disabled: Mapped[bool]
    is_admin: Mapped[bool]
    verification_hash: Mapped[str] = mapped_column(String(255))


class Benchmark(Base):
    __tablename__ = "benchmarks"
    id: Mapped[int] = mapped_column(Integer, primary_key=True, index=True)
    name: Mapped[str] = mapped_column(String(255), unique=True)
    description: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    function_count: Mapped[int]
    trial_count: Mapped[int]
    files: Mapped[List["LocalFile"]] = relationship("LocalFile", back_populates="benchmark")


class User(BaseModel):
    email: Union[str, None] = None
    disabled: Union[bool, None] = None
    is_admin: bool = False


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
    function_number: Optional[int]
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
