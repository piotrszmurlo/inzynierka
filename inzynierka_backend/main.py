import os
from typing import Annotated

import python_extensions as extensions
from fastapi import FastAPI, HTTPException, UploadFile, Form
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.exc import IntegrityError
from src import models
from src.FileService import FileService
from src.SQLAlchemyFileRepository import SQLAlchemyFileRepository, engine, SessionLocal
from src.models import ParseError
from src.parser import parse_remote_results_file, get_final_error_and_evaluation_number_for_files, \
    ALL_DIMENSIONS, TRIALS_COUNT, get_final_error_and_evaluations_number_array, \
    get_final_error_and_evaluation_number_for_files_grouped_by_algorithm, \
    map_statistic_ranking_entries_to_pydantic_model
from scipy.stats import wilcoxon

models.Base.metadata.create_all(bind=engine)
app = FastAPI()
ROOT_DIR = os.path.dirname(os.path.abspath(__file__))

repository = SQLAlchemyFileRepository(SessionLocal())
service = FileService(repository)

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
    return service.get_algorithm_names()


@app.get("/dimensions")
async def get_available_dimensions():
    return service.get_dimensions()


@app.get("/functions")
async def get_available_functions():
    return service.get_function_numbers()


@app.post("/rankings/wilcoxon")
async def get_wilcoxon_test(first_algorithm: Annotated[str, Form()], second_algorithm: Annotated[str, Form()],
                            dimension: Annotated[int, Form()], function_number: Annotated[int, Form()]):
    first_file = service.get_file(algorithm_name=first_algorithm, dimension=dimension, function_number=function_number)
    second_file = service.get_file(algorithm_name=second_algorithm, dimension=dimension,
                                   function_number=function_number)
    err1 = get_final_error_and_evaluations_number_array(first_file)
    err2 = get_final_error_and_evaluations_number_array(second_file)
    diff = []
    for index in range(len(err1)):
        diff.append(err1[index] - err2[index])
    try:
        h0_p_value = wilcoxon(diff)[1]
        if h0_p_value < 0.05:
            h1_p_value = wilcoxon(diff, alternative="less")[1]
            if h1_p_value < 0.05:
                return "-"
            return "+"

        else:
            return "="
    except ValueError as e:
        if "zero for all elements" in str(e):
            return "="
        raise HTTPException(422, detail=str(e))


@app.get("/rankings/cec2022")
async def get_cec2022_ranking():
    response = {"dimension": {}}
    for dimension in ALL_DIMENSIONS:
        errors = get_final_error_and_evaluation_number_for_files(
            service.get_files_for_dimension(dimension)
        )
        scores = extensions.calculate_cec2022_scores(
            TRIALS_COUNT, errors
        )
        response["dimension"][dimension] = [{"algorithmName": name, "score": score} for name, score in scores.items()]
    return response


@app.get("/rankings/friedman")
async def get_friedman_ranking():
    response = {"dimension": {}}
    for dimension in ALL_DIMENSIONS:
        results = get_final_error_and_evaluation_number_for_files(
            service.get_files_for_dimension(dimension)
        )
        scores = extensions.calculate_friedman_scores(
            TRIALS_COUNT, results
        )
        response["dimension"][dimension] = [{"algorithmName": name, "score": score} for name, score in scores.items()]
    return response


@app.get("/rankings/basic")
async def get_statistics_ranking_data():
    ranking_entries = extensions.calculate_statisticsV2(
        get_final_error_and_evaluation_number_for_files_grouped_by_algorithm(
            service.get_files()
        )
    )
    return map_statistic_ranking_entries_to_pydantic_model(ranking_entries)


@app.post("/file")
async def post_file(files: list[UploadFile]):
    try:
        for file in files:
            algorithm_name, function_number, dimension, parsed_contents = parse_remote_results_file(file.filename,
                                                                                                    await file.read())
            service.create_file(algorithm_name, dimension, function_number, parsed_contents)
    except IntegrityError:
        raise HTTPException(409, detail='File already exists')
    except ParseError as e:
        raise HTTPException(422, detail=str(e))
