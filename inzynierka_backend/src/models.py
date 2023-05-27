from pydantic import BaseModel


class RemoteDataFile(BaseModel):
    content: bytes
    name: str
    size: int