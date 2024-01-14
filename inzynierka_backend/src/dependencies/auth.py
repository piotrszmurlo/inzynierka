import smtplib
from typing import Annotated

from fastapi import Depends, HTTPException
from fastapi.security import OAuth2PasswordBearer
from jose import jwt, JWTError
from starlette import status

from src.models.user import User
from src.services.FileService import FileService
from src.dependencies.rankings import Rankings
from src.repositories_impl.SQLAlchemyFileRepository import SQLAlchemyFileRepository, SessionLocal
from src.repositories_impl.SQLAlchemyUserRepository import SQLAlchemyUserRepository
from src.services.UserService import UserService
from src.dependencies.auth_helpers import TokenData
from src.config import settings

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")

file_repository = SQLAlchemyFileRepository(SessionLocal())
file_service = FileService(file_repository)
rankings = Rankings(file_service)
user_repository = SQLAlchemyUserRepository(SessionLocal())
user_service = UserService(user_repository)


async def get_current_user(token: Annotated[str, Depends(oauth2_scheme)]):
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
    user = user_repository.get_user(email=token_data.email)
    if user is None:
        raise credentials_exception
    return user


async def get_current_active_user(
        current_user: Annotated[User, Depends(get_current_user)]
):
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
