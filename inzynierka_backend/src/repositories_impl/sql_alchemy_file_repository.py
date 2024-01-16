from typing import Optional

from sqlalchemy import create_engine, select, delete, and_, ScalarResult
from sqlalchemy.orm import sessionmaker, Session

from src.models.benchmark import Benchmark
from src.models.file import LocalFile
from src.repositories.file_repository import IFileRepository
from src.config import settings

engine = create_engine(settings.DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


class SQLAlchemyFileRepository(IFileRepository):

    def __init__(self, db: Session):
        self._db = db

    def get_file(self, algorithm_name: str, dimension: int, function_number: int, benchmark_name: str) -> Optional[LocalFile]:
        query = (select(LocalFile)
                 .filter_by(
                    algorithm_name=algorithm_name,
                    dimension=dimension,
                    function_number=function_number
                )
                 .join(LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name))
        return self._db.scalars(query).first()

    def get_files_for_dimension(self, dimension: int, benchmark_name: str):
        query = (select(LocalFile)
                 .filter_by(dimension=dimension)
                 .join(LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name))
        return self._db.scalars(query).all()

    def get_files_for_dimension_and_algorithm_name(self, dimension: int, algorithm_name: str, benchmark_name: str):
        query = (select(LocalFile)
                 .filter_by(dimension=dimension, algorithm_name=algorithm_name)
                 .join(LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name))
        return self._db.scalars(query).all()

    def get_benchmarks(self):
        query = select(Benchmark)
        return self._db.scalars(query).all()

    def create_benchmark(self, name: str, description: str, function_count: int, trial_count: int):
        try:
            benchmark = Benchmark(
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
        query = select(Benchmark).filter_by(name=benchmark_name)
        return self._db.scalars(query).first()

    def get_files(self, benchmark_name: str):
        query = (select(LocalFile)
                 .join(LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name))
        return self._db.scalars(query).all()

    def get_algorithm_names_for_user(self, benchmark_name: str, user_id: int):
        query = (select(LocalFile.algorithm_name)
                 .join(LocalFile.benchmark)
                 .where(and_(Benchmark.name == benchmark_name, LocalFile.owner_id == user_id))
                 .distinct(LocalFile.algorithm_name))
        return self._db.scalars(query).all()

    def get_algorithm_names(self, benchmark_name: str):
        query = (select(LocalFile.algorithm_name)
                 .join(LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name)
                 .distinct(LocalFile.algorithm_name))
        return self._db.scalars(query).all()

    def get_dimensions(self, benchmark_name: str):
        query = (select(LocalFile.dimension)
                 .join(LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name)
                 .distinct(LocalFile.dimension))
        return self._db.scalars(query).all()

    def get_function_numbers(self, benchmark_name: str):
        query = (select(LocalFile.function_number)
                 .join(LocalFile.benchmark)
                 .where(Benchmark.name == benchmark_name)
                 .distinct(LocalFile.function_number))
        return self._db.scalars(query).all()

    def create_file(self, algorithm_name: str, dimension: int, function_number: int, content: str, benchmark_id: int, owner_id: int):
        try:
            file = LocalFile(
                algorithm_name=algorithm_name,
                contents=content,
                dimension=dimension,
                function_number=function_number,
                benchmark_id=benchmark_id,
                owner_id=owner_id
            )
            self._db.add(file)
            self._db.commit()
        except:
            self._db.rollback()
            raise

    def delete_files_for_algorithm_name(self, algorithm_name: str, benchmark_name: str):
        try:
            benchmark_id = self.get_benchmark(benchmark_name).id
            query = (delete(LocalFile).where(
                and_
                (LocalFile.algorithm_name == algorithm_name,
                 LocalFile.benchmark_id == benchmark_id)
            ))
            self._db.execute(query)
            self._db.commit()
        except:
            self._db.rollback()
            raise

    def get_files_for_algorithm_name(self, algorithm_name: str, benchmark_name: str) -> ScalarResult[LocalFile]:
        benchmark_id = self.get_benchmark(benchmark_name).id
        query = (select(LocalFile).where(
            and_
            (LocalFile.algorithm_name == algorithm_name,
             LocalFile.benchmark_id == benchmark_id)
        ))
        return self._db.scalars(query)

    def delete_benchmark(self, benchmark_name: str):
        try:
            query = (delete(Benchmark).where(
                 Benchmark.name == benchmark_name)
            )
            self._db.execute(query)
            self._db.commit()
        except:
            self._db.rollback()
            raise
