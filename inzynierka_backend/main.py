import os
from typing import Annotated

from fastapi import FastAPI, HTTPException, UploadFile, Form
from fastapi.middleware.cors import CORSMiddleware
from fastapi.params import Depends
from fastapi.security import OAuth2PasswordBearer, OAuth2PasswordRequestForm
from jose import jwt, JWTError
from sqlalchemy.exc import IntegrityError
from starlette import status

from src import models
from src.FileService import FileService
from src.Rankings import Rankings
from src.SQLAlchemyFileRepository import SQLAlchemyFileRepository, engine, SessionLocal
from src.SQLAlchemyUserRepository import SQLAlchemyUserRepository
from src.UserService import UserService
from src.auth_helpers import TokenData, Token, authenticate_user, create_access_token, \
    get_password_hash
from src.config import settings
from src.models import ParseError, User
from src.parser import parse_remote_results_file, ALL_DIMENSIONS, FUNCTIONS_COUNT, parse_remote_filename, \
    check_filenames_integrity

models.Base.metadata.create_all(bind=engine)
app = FastAPI()
ROOT_DIR = os.path.dirname(os.path.abspath(__file__))

file_repository = SQLAlchemyFileRepository(SessionLocal())
file_service = FileService(file_repository)

user_repository = SQLAlchemyUserRepository(SessionLocal())
user_service = UserService(user_repository)

rankings = Rankings(file_service)

app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

oauth2_scheme = OAuth2PasswordBearer(tokenUrl="token")


async def get_current_user(token: Annotated[str, Depends(oauth2_scheme)]):
    credentials_exception = HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="Could not validate credentials",
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


@app.post("/token", response_model=Token)
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


@app.post("/register", response_model=Token)
async def login_for_access_token(form_data: Annotated[OAuth2PasswordRequestForm, Depends()]):
    user = user_repository.get_user(form_data.username)
    if user:
        raise HTTPException(
            status_code=status.HTTP_409_CONFLICT,
            detail="User already exists"
        )
    user_repository.create_user(email=form_data.username, password_hash=get_password_hash(form_data.password),
                                disabled=True, is_admin=False)
    access_token = create_access_token(
        data={"sub": form_data.username}
    )
    return {"access_token": access_token, "token_type": "bearer"}


@app.get("/users/me")
async def get_current_user_data(current_user: Annotated[User, Depends(get_current_user)]):
    return User(email=current_user.email, disabled=current_user.disabled, is_admin=current_user.is_admin)


@app.get("/users")
async def get_all_users(current_user: Annotated[User, Depends(get_current_active_user)]):
    if not current_user.is_admin:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Current user does not have permission to perform this action"
        )
    return user_service.get_users()


@app.post("/users/promote")
async def promote_user(email: str, current_user: Annotated[User, Depends(get_current_active_user)]):
    if not current_user.is_admin:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Current user does not have permission to perform this action"
        )
    user = user_service.get_user(email)
    if not user:
        raise HTTPException(
            status_code=status.HTTP_404_NOT_FOUND,
            detail=f"User with email: {email} does not exist"
        )
    user_service.promote_user_to_admin(email)
    return True


@app.get("/algorithms")
async def get_available_algorithms():
    return file_service.get_algorithm_names()


@app.get("/dimensions")
async def get_available_dimensions():
    return file_service.get_dimensions()


@app.get("/functions")
async def get_available_functions():
    return file_service.get_function_numbers()


@app.post("/rankings/wilcoxon")
async def get_wilcoxon_test(
        first_algorithm: Annotated[str, Form()],
        second_algorithm: Annotated[str, Form()],
        dimension: Annotated[int, Form()]
):
    try:
        return rankings.get_wilcoxon_test(
            first_algorithm=first_algorithm,
            second_algorithm=second_algorithm,
            dimension=dimension
        )
    except ValueError as e:
        raise HTTPException(422, detail=str(e))


@app.get("/rankings/cec2022")
async def get_cec2022_ranking():
    return rankings.get_cec2022_ranking_scores()


@app.get("/rankings/friedman")
async def get_friedman_ranking():
    return rankings.get_friedman_ranking_scores()


@app.get("/rankings/statistics")
async def get_statistics_ranking_data():
    return rankings.get_statistics_ranking_data()


@app.get("/rankings/revisited")
async def get_revisited_ranking():
    return rankings.get_revisited_ranking_entries()


@app.get("/rankings/ecdf")
async def get_ecdf_data():
    return rankings.get_ecdf_data()


@app.delete("/file/{algorithm_name}")
async def delete_files(algorithm_name: str, current_user: Annotated[User, Depends(get_current_active_user)]):
    if not current_user.is_admin:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Current user does not have permission to perform this action"
        )
    file_service.delete_files(algorithm_name)
    rankings.invalidate_cache()


@app.post("/file")
async def post_file(files: list[UploadFile], overwrite: bool,
                    current_user: Annotated[User, Depends(get_current_active_user)]):
    try:
        if len(files) != FUNCTIONS_COUNT * len(ALL_DIMENSIONS):
            raise ParseError(
                f"Provide exactly {FUNCTIONS_COUNT * len(ALL_DIMENSIONS)} files, one for each function-dimension pair"
            )
        check_filenames_integrity(
            [parse_remote_filename(file.filename) for file in files])
        parsed_file_tuples = []
        for file in files:
            parsed_file_tuples.append(
                parse_remote_results_file(
                    file.filename, await file.read()
                )
            )
        if overwrite:
            file_service.delete_files(parsed_file_tuples[0][0])
        for algorithm_name, function_number, dimension, content in parsed_file_tuples:
            file_service.create_file(algorithm_name=algorithm_name, function_number=function_number,
                                     dimension=dimension, content=content)
        rankings.invalidate_cache()
    except IntegrityError:
        raise HTTPException(409, detail='File already exists')
    except ParseError as e:
        raise HTTPException(422, detail=str(e))
