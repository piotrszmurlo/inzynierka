from pydantic import BaseModel
from sqlalchemy import Column, Integer, Text, UniqueConstraint
from sqlalchemy.orm import declarative_base
from sqlalchemy.dialects.mysql import VARCHAR

Base = declarative_base()


class ParseError(Exception):
    pass


class RemoteDataFile(BaseModel):
    content: bytes
    name: str
    size: int


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
