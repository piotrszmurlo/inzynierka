from typing import Union

from jose import jwt
from passlib.context import CryptContext
from pydantic import BaseModel

from src import IUserRepository
from src.config import settings

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")


class Token(BaseModel):
    access_token: str
    token_type: str


class TokenData(BaseModel):
    email: Union[str, None] = None


def get_password_hash(password):
    return pwd_context.hash(password)


def authenticate_user(repository: IUserRepository, email: str, password: str):
    user = repository.get_user(email)
    if not user:
        return False
    if not verify_password(password, user.password_hash):
        return False
    return user


def create_access_token(data: dict):
    to_encode = data.copy()
    encoded_jwt = jwt.encode(to_encode, settings.JWT_SECRET_KEY, algorithm=settings.HASH_ALGORITHM)
    return encoded_jwt


def verify_password(plain_password, password_hash):
    return pwd_context.verify(plain_password, password_hash)
