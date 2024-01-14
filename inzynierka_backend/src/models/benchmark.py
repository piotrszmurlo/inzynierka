from typing import Optional, List

from sqlalchemy import Integer, String
from sqlalchemy.orm import relationship, Mapped, mapped_column

from src.models.base import Base
from src.models.file import LocalFile


class Benchmark(Base):
    __tablename__ = "benchmarks"
    id: Mapped[int] = mapped_column(Integer, primary_key=True, index=True)
    name: Mapped[str] = mapped_column(String(255), unique=True)
    description: Mapped[Optional[str]] = mapped_column(String(255), nullable=True)
    function_count: Mapped[int]
    trial_count: Mapped[int]
    files: Mapped[List["LocalFile"]] = relationship("LocalFile", back_populates="benchmark")
