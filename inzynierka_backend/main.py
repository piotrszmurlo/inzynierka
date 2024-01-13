import asyncio
import os
import smtplib
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
    get_password_hash, generate_verification_code, verify_password
from src.config import settings
from src.models import ParseError, User
from src.parser import parse_remote_results_file, ALL_DIMENSIONS, parse_remote_filename, \
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


@app.post("/users/verify")
async def login_for_access_token(code: str, current_user: Annotated[User, Depends(get_current_user)]):
    user = user_service.get_user(current_user.email)
    if user.verification_hash == code:
        user_service.verify_user(current_user.email)
    else:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="Incorrect verification code"
        )


@app.post("/users/password")
async def change_password(new_password: Annotated[str, Form()], old_password: Annotated[str, Form()],
                          current_user: Annotated[User, Depends(get_current_user)]):
    user = user_service.get_user(current_user.email)
    if not verify_password(old_password, user.password_hash):
        raise HTTPException(
            status_code=status.HTTP_422_UNPROCESSABLE_ENTITY,
            detail="Incorrect password"
        )
    user_service.change_password(user.email, get_password_hash(new_password))


@app.post("/users/email")
async def change_email(new_email: Annotated[str, Form()],
                       current_user: Annotated[User, Depends(get_current_active_user)]):
    try:
        user = user_service.get_user(current_user.email)
        user_service.change_email(user.email, new_email)
        access_token = create_access_token(
            data={"sub": user.email}
        )
        return {"access_token": access_token, "token_type": "bearer"}
    except IntegrityError:
        raise HTTPException(409, detail='User with this email already exists')


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


@app.get("/users/resend")
async def resend_verification_code(current_user: Annotated[User, Depends(get_current_user)]):
    asyncio.create_task(
        send_verification_email(
            current_user.email,
            current_user.verification_hash
        )
    )


@app.post("/register", response_model=Token)
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


@app.get("/users/me")
async def get_current_user_data(current_user: Annotated[User, Depends(get_current_user)]):
    return User(email=current_user.email, disabled=current_user.disabled, is_admin=current_user.is_admin)


@app.get("/benchmarks")
async def get_all_benchmarks():
    return file_service.get_benchmarks()


@app.delete("/benchmarks/{benchmark_name}")
async def delete_benchmark(benchmark_name: str):
    return file_service.delete_benchmark(benchmark_name)


@app.post("/benchmarks")
async def create_benchmark(name: Annotated[str, Form()], description: Annotated[str, Form()],
                           function_count: Annotated[int, Form()], trial_count: Annotated[int, Form()]):
    try:
        file_service.create_benchmark(name, description, function_count, trial_count)
    except IntegrityError:
        raise HTTPException(409, detail='Benchmark with this name already exists')


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


@app.get("/algorithms/{benchmark_name}")
async def get_available_algorithms(benchmark_name: str):
    return file_service.get_algorithm_names(benchmark_name)


@app.get("/dimensions/{benchmark_name}")
async def get_available_dimensions(benchmark_name: str):
    return file_service.get_dimensions(benchmark_name)


@app.get("/functions/{benchmark_name}")
async def get_available_functions(benchmark_name: str):
    return file_service.get_function_numbers(benchmark_name)


@app.post("/rankings/wilcoxon")
async def get_wilcoxon_test(
        benchmark_name: Annotated[str, Form()],
        first_algorithm: Annotated[str, Form()],
        second_algorithm: Annotated[str, Form()],
        dimension: Annotated[int, Form()]
):
    try:
        benchmark = file_service.get_benchmark(benchmark_name)
        return rankings.get_wilcoxon_test(
            first_algorithm=first_algorithm,
            second_algorithm=second_algorithm,
            dimension=dimension,
            benchmark=benchmark
        )
    except ValueError as e:
        raise HTTPException(422, detail=str(e))


@app.get("/rankings/cec2022")
async def get_cec2022_ranking(benchmark_name: str):
    benchmark = file_service.get_benchmark(benchmark_name)
    return rankings.get_cec2022_ranking_scores(benchmark)


@app.get("/rankings/friedman")
async def get_friedman_ranking(benchmark_name: str):
    benchmark = file_service.get_benchmark(benchmark_name)
    return rankings.get_friedman_ranking_scores(benchmark)


@app.get("/rankings/statistics")
async def get_statistics_ranking_data(benchmark_name: str):
    benchmark = file_service.get_benchmark(benchmark_name)
    return rankings.get_statistics_ranking_data(benchmark)


@app.get("/rankings/revisited")
async def get_revisited_ranking(benchmark_name: str):
    benchmark = file_service.get_benchmark(benchmark_name)
    return rankings.get_revisited_ranking_entries(benchmark)


@app.get("/rankings/ecdf")
async def get_ecdf_data(benchmark_name: str):
    benchmark = file_service.get_benchmark(benchmark_name)
    return rankings.get_ecdf_data(benchmark)


@app.delete("/file/{algorithm_name}")
async def delete_files(algorithm_name: str, benchmark_name: str,
                       current_user: Annotated[User, Depends(get_current_active_user)]):
    if not current_user.is_admin:
        raise HTTPException(
            status_code=status.HTTP_403_FORBIDDEN,
            detail="Current user does not have permission to perform this action"
        )
    file_service.delete_files(algorithm_name, benchmark_name)


@app.post("/file")
async def post_file(files: list[UploadFile], benchmark: str, overwrite: bool,
                    current_user: Annotated[User, Depends(get_current_active_user)]):
    try:
        benchmark_data = file_service.get_benchmark(benchmark)
        if not benchmark_data:
            raise HTTPException(404, detail='Given benchmark does not exist')
        if len(files) != benchmark_data.function_count * len(ALL_DIMENSIONS):
            raise ParseError(
                f"Provide exactly {benchmark_data.function_count * len(ALL_DIMENSIONS)} files, one for each function-dimension pair"
            )
        check_filenames_integrity(
            [parse_remote_filename(file.filename) for file in files],
            benchmark_data.function_count
        )
        parsed_file_tuples = []
        for file in files:
            parsed_file_tuples.append(
                parse_remote_results_file(
                    file.filename, await file.read(), benchmark_data.trial_count
                )
            )
        if overwrite:
            file_service.delete_files(parsed_file_tuples[0][0], benchmark_data.name)
        for algorithm_name, function_number, dimension, content in parsed_file_tuples:
            file_service.create_file(algorithm_name=algorithm_name, function_number=function_number,
                                     dimension=dimension, content=content, benchmark_id=benchmark_data.id)
    except IntegrityError:
        raise HTTPException(409, detail='File already exists')
    except ParseError as e:
        raise HTTPException(422, detail=str(e))
    except UnicodeDecodeError as e:
        raise HTTPException(422, detail=str(e))
