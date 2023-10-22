import os
from pprint import pprint
from typing import Annotated

import python_extensions as extensions
from fastapi import FastAPI, Depends, HTTPException, UploadFile, Form
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session
from src import models
from src.dbaccess import engine, SessionLocal, get_files_for_dimension, get_all_algorithm_names, create_file, get_file
from src.models import ParseError
from src.parser import get_updated_rankings, parse_remote_results_file, get_final_error_and_evaluation_number_for_files, \
    ALL_DIMENSIONS, TRIALS_COUNT, get_final_error_and_evaluations_number_numpy
from scipy.stats import wilcoxon


models.Base.metadata.create_all(bind=engine)
app = FastAPI()
ROOT_DIR = os.path.dirname(os.path.abspath(__file__))


def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()


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


@app.get("/rankings")
async def get_rankings(db: Session = Depends(get_db)):
    medians, averages, cec2022, friedman = get_updated_rankings(db)
    return {
        "average": averages,
        "medians": medians,
        "cec2022": cec2022,
        "friedman": friedman
    }


@app.get("/algorithms")
async def get_available_algorithms(db: Session = Depends(get_db)):
    return get_all_algorithm_names(db)


@app.post("/rankings/wilcoxon")
async def get_wilcoxon_test(first_algorithm: Annotated[str, Form()], second_algorithm: Annotated[str, Form()], dimension: Annotated[int, Form()], function_number: Annotated[int, Form()], db: Session = Depends(get_db)):
    file = get_file(db, algorithm_name=first_algorithm, dimension=dimension, function_number=function_number)
    file2 = get_file(db, algorithm_name=second_algorithm, dimension=dimension, function_number=function_number)
    err1 = get_final_error_and_evaluations_number_numpy(file)
    err2 = get_final_error_and_evaluations_number_numpy(file2)
    diff = []
    for index in range(len(err1)):
        diff.append(err1[index] - err2[index])
    h0_p_value = wilcoxon(diff)[1]
    if h0_p_value < 0.05:
        h1_p_value = wilcoxon(diff, alternative="less")[1]
        if h1_p_value < 0.05:
            return "-"
        return "+"

    else:
        return "="




@app.get("/rankings/cec2022")
async def get_cec2022_ranking(db: Session = Depends(get_db)):
    response = {"dimension": {}}
    for dimension in ALL_DIMENSIONS:
        errors = get_final_error_and_evaluation_number_for_files(
            get_files_for_dimension(db, dimension)
        )
        scores = extensions.calculate_cec2022_scores(
            TRIALS_COUNT, errors
        )
        response["dimension"][dimension] = [{"algorithmName": name, "score": score} for name, score in scores.items()]
    return response


@app.get("/rankings/friedman")
async def get_friedman_ranking(db: Session = Depends(get_db)):
    friedman = {}
    for dimension in ALL_DIMENSIONS:
        results = get_final_error_and_evaluation_number_for_files(
            get_files_for_dimension(db, dimension)
        )
        friedman[dimension] = extensions.calculate_friedman_scores(
            TRIALS_COUNT, results)
    return friedman


@app.post("/file")
async def post_file(files: list[UploadFile], db: Session = Depends(get_db)):
    try:
        for file in files:
            algorithm_name, function_number, dimension, parsed_contents = parse_remote_results_file(file.filename, await file.read())
            create_file(db, algorithm_name, dimension, function_number, parsed_contents)
    except IntegrityError:
        raise HTTPException(409, detail='File already exists')
    except ParseError as e:
        raise HTTPException(422, detail=str(e))
    return {"data": [1, 2, 3]}
