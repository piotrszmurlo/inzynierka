import base64
from itertools import groupby
from operator import attrgetter
from pprint import pprint

import numpy as np
from fastapi import FastAPI, Depends, HTTPException, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import sessionmaker, Session
from src.dbaccess import create_file, get_file, get_all_files
import python_extensions as extensions
from src import models
from src.dbaccess import engine, SessionLocal
from src.models import RemoteDataFile, ParseError, LocalFile
from src.parser import get_updated_rankings, get_final_error_and_evaluation_number_for_files, \
    parse_remote_results_fileV2

models.Base.metadata.create_all(bind=engine)
app = FastAPI()


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
    # calculate_cec_ranking(db)
    medians, averages, cec2022 = get_updated_rankings(get_all_files(db), db)
    return {
        "average": averages,
        "medians": medians,
        "cec2022": cec2022
    }


@app.post("/file")
async def post_file(files: list[UploadFile]):
    try:
        for file in files:
            print(file.filename)
    except IntegrityError:
        raise HTTPException(409, detail='File already exists')
    except ParseError as e:
        raise HTTPException(422, detail=str(e))
    return {"data": [1, 2, 3]}
