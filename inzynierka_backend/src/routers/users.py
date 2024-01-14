import asyncio
from typing import Annotated

from fastapi import APIRouter, Depends, HTTPException, Form
from fastapi.security import OAuth2PasswordRequestForm
from sqlalchemy.exc import IntegrityError
from starlette import status

from src.dependencies.auth_helpers import verify_password, get_password_hash, generate_verification_code, create_access_token, Token, \
    authenticate_user
from src.dependencies.auth import get_current_active_user, user_service, get_current_user, send_verification_email, \
    user_repository
from src.models.user import User

router = APIRouter(prefix='/users')


@router.get("/")
async def get_all_users(current_user: Annotated[User, Depends(get_current_active_user)]):
    if not current_user.is_admin:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Current user does not have permission to perform this action"
        )
    return user_service.get_users()


@router.post("/promote")
async def promote_user(email: str, current_user: Annotated[User, Depends(get_current_active_user)]):
    if not current_user.is_admin:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Current user does not have permission to perform this action"
        )
    user = user_service.get_user(email)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail=f"User with email: {email} is not verified"
        )
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"User with email: {email} does not exist"
        )
    user_service.promote_user_to_admin(email)
    return True


@router.post("/password")
async def change_password(new_password: Annotated[str, Form()], old_password: Annotated[str, Form()],
                          current_user: Annotated[User, Depends(get_current_user)]):
    user = user_service.get_user(current_user.email)
    if verify_password(new_password, user.password_hash):
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="New password must be different than previous one"
        )
    if not verify_password(old_password, user.password_hash):
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="Incorrect password"
        )
    user_service.change_password(user.email, get_password_hash(new_password))


@router.post("/email")
async def change_email(new_email: Annotated[str, Form()],
                       current_user: Annotated[User, Depends(get_current_user)]):
    try:
        user = user_service.get_user(current_user.email)
        code = generate_verification_code()
        user_service.change_email(user.email, new_email, code)
        access_token = create_access_token(
            data={"sub": new_email}
        )

        asyncio.create_task(send_verification_email(new_email, code))
        return {"access_token": access_token, "token_type": "bearer"}
    except IntegrityError:
        raise HTTPException(409, detail='User with this email already exists')


@router.post("/verify")
async def login_for_access_token(code: str, current_user: Annotated[User, Depends(get_current_user)]):
    user = user_service.get_user(current_user.email)
    if user.verification_hash == code:
        user_service.verify_user(current_user.email)
    else:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Incorrect verification code"
        )


@router.get("/me")
async def get_current_user_data(current_user: Annotated[User, Depends(get_current_user)]):
    return User(email=current_user.email, disabled=current_user.disabled, is_admin=current_user.is_admin)


@router.get("/resend")
async def resend_verification_code(current_user: Annotated[User, Depends(get_current_user)]):
    asyncio.create_task(
        send_verification_email(
            current_user.email,
            current_user.verification_hash
        )
    )


@router.post("/register", response_model=Token)
async def login_for_access_token(form_data: Annotated[OAuth2PasswordRequestForm, Depends()]):
    user = user_repository.get_user(form_data.username)
    if user:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="User already exists"
        )

    code = generate_verification_code()

    asyncio.create_task(send_verification_email(form_data.username, code))
    user_repository.create_user(email=form_data.username, password_hash=get_password_hash(form_data.password),
                                verification_hash=code,
                                disabled=True, is_admin=False)
    access_token = create_access_token(
        data={"sub": form_data.username}
    )
    return {"access_token": access_token, "token_type": "bearer"}


@router.post("/login", response_model=Token)
async def login_for_access_token(form_data: Annotated[OAuth2PasswordRequestForm, Depends()]):
    user = authenticate_user(user_repository, form_data.username, form_data.password)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_401_UNAUTHORIZED,
            detail="Incorrect email or password",
            headers={"WWW-Authenticate": "Bearer"},
        )
    access_token = create_access_token(
        data={"sub": user.email}
    )
    return {"access_token": access_token, "token_type": "bearer"}
