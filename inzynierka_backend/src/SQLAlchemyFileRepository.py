from typing import Optional

from sqlalchemy import create_engine, select, delete
from sqlalchemy.orm import sessionmaker, Session

from src import models
from src.IFileRepository import IFileRepository
from src.config import settings

engine = create_engine(settings.DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)


class SQLAlchemyFileRepository(IFileRepository):

    def __init__(self, db: Session):
        self._db = db

    def get_file(self, algorithm_name: str, dimension: int, function_number: int) -> Optional[models.LocalFile]:
        query = select(models.LocalFile).filter_by(
            algorithm_name=algorithm_name,
            dimension=dimension,
            function_number=function_number
        )
        return self._db.scalars(query).first()

    def get_files_for_dimension(self, dimension: int):
        query = select(models.LocalFile).filter_by(dimension=dimension)
        return self._db.scalars(query).all()

    def get_files_for_dimension_and_algorithm_name(self, dimension: int, algorithm_name: str):
        query = select(models.LocalFile).filter_by(dimension=dimension, algorithm_name=algorithm_name)
        return self._db.scalars(query).all()

    def get_files(self):
        query = select(models.LocalFile)
        return self._db.scalars(query).all()

    def get_algorithm_names(self):
        query = select(models.LocalFile.algorithm_name).distinct(models.LocalFile.algorithm_name)
        return self._db.scalars(query).all()

    def get_dimensions(self):
        query = select(models.LocalFile.dimension).distinct(models.LocalFile.dimension)
        return self._db.scalars(query).all()

    def get_function_numbers(self):
        query = select(models.LocalFile.function_number).distinct(models.LocalFile.function_number)
        return self._db.scalars(query).all()

    def create_file(self, algorithm_name: str, dimension: int, function_number: int, content: str):
        try:
            file = models.LocalFile(
                algorithm_name=algorithm_name,
                contents=content,
                dimension=dimension,
                function_number=function_number
            )
            self._db.add(file)
            self._db.commit()
        except:
            self._db.rollback()
            raise

    def delete_files_for_algorithm_name(self, algorithm_name: str):
        try:
            query = delete(models.LocalFile).where(models.LocalFile.algorithm_name == algorithm_name)
            self._db.execute(query)
            self._db.commit()
        except:
            self._db.rollback()
            raise