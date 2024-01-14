from typing import Union

from pydantic import BaseModel
from sqlalchemy import Integer, String
from sqlalchemy.orm import Mapped, mapped_column

from src.models.base import Base


class UserTable(Base):
    __tablename__ = "users"
    id: Mapped[int] = mapped_column(Integer, primary_key=True, index=True)
    email: Mapped[str] = mapped_column(String(255), unique=True)
    password_hash: Mapped[str] = mapped_column(String(255))
    disabled: Mapped[bool]
    is_admin: Mapped[bool]
    verification_hash: Mapped[str] = mapped_column(String(255))


class User(BaseModel):
    email: Union[str, None] = None
    disabled: Union[bool, None] = None
    is_admin: bool = False
