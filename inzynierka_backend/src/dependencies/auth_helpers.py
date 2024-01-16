import secrets
import string
from typing import Union

from jose import jwt
from passlib.context import CryptContext
from pydantic import BaseModel

from src.repositories import user_repository
from src.config import settings
from src.repositories.user_repository import IUserRepository

pwd_context = CryptContext(schemes=["bcrypt"], deprecated="auto")

CODE_LENGTH = 15


class Token(BaseModel):
    access_token: str
    token_type: str


def generate_verification_code():
    return ''.join(secrets.choice(string.ascii_uppercase + string.ascii_lowercase) for _ in range(CODE_LENGTH))


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
