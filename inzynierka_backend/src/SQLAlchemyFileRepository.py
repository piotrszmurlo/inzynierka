from typing import Optional

from sqlalchemy import create_engine, select, delete, and_
from sqlalchemy.orm import sessionmaker, Session

from src import models, Base, Benchmark
from src.IFileRepository import IFileRepository
from src.config import settings

engine = create_engine(settings.DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


class SQLAlchemyFileRepository(IFileRepository):

    def __init__(self, db: Session):
        self._db = db

    def get_file(self, algorithm_name: str, dimension: int, function_number: int, benchmark_name: str) -> Optional[models.LocalFile]:
        query = (select(models.LocalFile)
                 .filter_by(
                    algorithm_name=algorithm_name,
                    dimension=dimension,
                    function_number=function_number
                )
                 .join(models.LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name))
        return self._db.scalars(query).first()

    def get_files_for_dimension(self, dimension: int, benchmark_name: str):
        query = (select(models.LocalFile)
                 .filter_by(dimension=dimension)
                 .join(models.LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name))
        return self._db.scalars(query).all()

    def get_files_for_dimension_and_algorithm_name(self, dimension: int, algorithm_name: str, benchmark_name: str):
        query = (select(models.LocalFile)
                 .filter_by(dimension=dimension, algorithm_name=algorithm_name)
                 .join(models.LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name))
        return self._db.scalars(query).all()

    def get_benchmarks(self):
        query = select(models.Benchmark)
        return self._db.scalars(query).all()

    def create_benchmark(self, name: str, description: str, function_count: int, trial_count: int):
        try:
            benchmark = models.Benchmark(
                name=name,
                description=description,
                function_count=function_count,
                trial_count=trial_count
            )
            self._db.add(benchmark)
            self._db.commit()
        except:
            self._db.rollback()
            raise

    def get_benchmark(self, benchmark_name: str):
        query = select(models.Benchmark).filter_by(name=benchmark_name)
        return self._db.scalars(query).first()

    def get_files(self, benchmark_name: str):
        query = (select(models.LocalFile)
                 .join(models.LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name))
        return self._db.scalars(query).all()

    def get_algorithm_names(self, benchmark_name: str):
        query = (select(models.LocalFile.algorithm_name)
                 .join(models.LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name)
                 .distinct(models.LocalFile.algorithm_name))
        return self._db.scalars(query).all()

    def get_dimensions(self, benchmark_name: str):
        query = (select(models.LocalFile.dimension)
                 .join(models.LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name)
                 .distinct(models.LocalFile.dimension))
        return self._db.scalars(query).all()

    def get_function_numbers(self, benchmark_name: str):
        query = (select(models.LocalFile.function_number)
                 .join(models.LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name)
                 .distinct(models.LocalFile.function_number))
        return self._db.scalars(query).all()

    def create_file(self, algorithm_name: str, dimension: int, function_number: int, content: str, benchmark_id: int):
        try:
            file = models.LocalFile(
                algorithm_name=algorithm_name,
                contents=content,
                dimension=dimension,
                function_number=function_number,
                benchmark_id=benchmark_id
            )
            self._db.add(file)
            self._db.commit()
        except:
            self._db.rollback()
            raise

    def delete_files_for_algorithm_name(self, algorithm_name: str, benchmark_name: str):
        try:
            benchmark_id = self.get_benchmark(benchmark_name).id
            query = (delete(models.LocalFile).where(
                and_
                (models.LocalFile.algorithm_name == algorithm_name,
                 models.LocalFile.benchmark_id == benchmark_id)
            ))
            self._db.execute(query)
            self._db.commit()
        except:
            self._db.rollback()
            raise

    def delete_benchmark(self, benchmark_name: str):
        try:
            query = (delete(models.Benchmark).where(
                 models.Benchmark.name == benchmark_name)
            )
            self._db.execute(query)
            self._db.commit()
        except:
            self._db.rollback()
            raise
