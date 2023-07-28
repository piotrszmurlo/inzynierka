from pydantic import BaseModel
from sqlalchemy import Column, Integer, Text
from sqlalchemy.orm import declarative_base

Base = declarative_base()


class RemoteDataFile(BaseModel):
    content: bytes
    name: str
    size: int


class LocalFile(Base):
    __tablename__ = "files"
    id = Column(Integer, primary_key=True, index=True)
    contents = Column(Text)
    algorithm_name = Column(Text)
    dimension = Column(Integer)
    function_number = Column(Integer)
