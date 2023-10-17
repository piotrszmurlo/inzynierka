import os
from pprint import pprint

import python_extensions as extensions
from fastapi import FastAPI, Depends, HTTPException, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import Session
from src import models
from src.dbaccess import engine, SessionLocal, get_files_for_dimension, get_all_algorithm_names
from src.models import ParseError
from src.parser import get_updated_rankings, parse_remote_results_file, get_final_error_and_evaluation_number_for_files, \
    ALL_DIMENSIONS, TRIALS_COUNT

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


@app.get("/")
async def root():
    return {"message": f"Hello {13}"}


@app.get("/data")
async def root():
    return {"data": [44, 44, 44, 44]}


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
async def post_file(files: list[UploadFile]):
    try:
        for file in files:
            print(parse_remote_results_file(file.filename, await file.read())[-1])
    except IntegrityError:
        raise HTTPException(409, detail='File already exists')
    except ParseError as e:
        raise HTTPException(422, detail=str(e))
    return {"data": [1, 2, 3]}
