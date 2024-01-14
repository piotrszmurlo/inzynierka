from sqlalchemy import UniqueConstraint, Integer, Text, String, ForeignKey
from sqlalchemy.orm import Mapped, mapped_column, relationship

from src.models.base import Base


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

