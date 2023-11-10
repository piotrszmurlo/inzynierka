import os
from typing import Annotated

from fastapi import FastAPI, HTTPException, UploadFile, Form
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.exc import IntegrityError
from src import models
from src.FileService import FileService
from src.Rankings import Rankings
from src.SQLAlchemyFileRepository import SQLAlchemyFileRepository, engine, SessionLocal
from src.models import ParseError
from src.parser import parse_remote_results_file, ALL_DIMENSIONS, FUNCTIONS_COUNT, parse_remote_filename, check_filenames_integrity

models.Base.metadata.create_all(bind=engine)
app = FastAPI()
ROOT_DIR = os.path.dirname(os.path.abspath(__file__))

file_repository = SQLAlchemyFileRepository(SessionLocal())
file_service = FileService(file_repository)
rankings = Rankings(file_service)

origins = [
    "localhost:3000",
    "http://localhost:3000",
]

app.add_middleware(
    CORSMiddleware,
    allow_origins=origins,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


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


@app.post("/file")
async def post_file(files: list[UploadFile]):
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
        for algorithm_name, function_number, dimension, content in parsed_file_tuples:
            file_service.create_file(algorithm_name=algorithm_name, function_number=function_number, dimension=dimension, content=content)
        rankings.invalidate_cache()
    except IntegrityError:
        raise HTTPException(409, detail='File already exists')
    except ParseError as e:
        raise HTTPException(422, detail=str(e))
