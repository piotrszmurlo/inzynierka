import smtplib
from typing import Annotated

from fastapi import Depends, HTTPException
from fastapi.security import OAuth2PasswordBearer
from jose import jwt, JWTError
from starlette import status

from src.dependencies.rankings_service import RankingsService
from src.models.user import User
from src.dependencies.auth_helpers import TokenData
from src.config import settings
from src.repositories_impl.sql_alchemy_file_repository import SessionLocal, SQLAlchemyFileRepository
from src.repositories_impl.sql_alchemy_user_repository import SQLAlchemyUserRepository
from src.services.FileService import FileService
from src.services.UserService import UserService

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

session = SessionLocal()
file_repository = SQLAlchemyFileRepository(session)
file_service = FileService(file_repository)
rankings_service = RankingsService(file_service)
user_repository = SQLAlchemyUserRepository(session)
user_service = UserService(user_repository)


def get_user_service():
    return user_service


def get_file_repository():
    return SQLAlchemyFileRepository(session)


def get_file_service():
    return file_service


def get_rankings_service():
    return rankings_service


FileServiceDep = Depends(get_file_service)
RankingsServiceDep = Depends(get_rankings_service)


UserServiceDep = Depends(get_user_service)


async def get_current_user(token: Annotated[str, Depends(oauth2_scheme)], user_service: UserService = UserServiceDep):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Can't authenticate user",
        headers={"WWW-Authenticate": "Bearer"},
    )
    try:
        payload = jwt.decode(token, settings.JWT_SECRET_KEY, algorithms=[settings.HASH_ALGORITHM])
        email: str = payload.get("sub")
        if email is None:
            raise credentials_exception
        token_data = TokenData(email=email)
    except JWTError:
        raise credentials_exception
    user = user_service.get_user(email=token_data.email)
    if user is None:
        raise credentials_exception
    return user



CurrentUserDep = Annotated[User, Depends(get_current_user)]

async def get_current_active_user(current_user: CurrentUserDep):
    if current_user.disabled:
        raise HTTPException(status_code=400, detail="Inactive user")
    return current_user


async def send_verification_email(email: str, code: str):
    try:
        with smtplib.SMTP(settings.EMAIL_HOST, settings.EMAIL_PORT) as server:
            try:
                server.login(settings.EMAIL_USERNAME, settings.EMAIL_PASSWORD)
            except smtplib.SMTPNotSupportedError:
                pass
            server.sendmail(settings.EMAIL_FROM, email, f"Verification code: {code}")
    except ConnectionRefusedError:
        print("SMTP server refused to connect")

CurrentActiveUserDep = Annotated[User, Depends(get_current_active_user)]
