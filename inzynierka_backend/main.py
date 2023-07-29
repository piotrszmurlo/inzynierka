import base64
from pprint import pprint

from fastapi import FastAPI, Depends, HTTPException
import python_example as p
from fastapi.middleware.cors import CORSMiddleware
from multipart.exceptions import ParseError
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import sessionmaker, Session
from src.dbaccess import create_file, get_file, get_all_files

from src import models
from src.dbaccess import engine, SessionLocal
from src.models import RemoteDataFile
from src.parser import parse_results_file, update_rankings

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
    return {"message": f"Hello {p.subtract(100, 33)}"}


@app.get("/rankings")
async def get_rankings(db: Session = Depends(get_db)):
    medians, averages = update_rankings(get_all_files(db))
    return {
        "average": averages,
        "medians": medians
    }


@app.post("/file")
async def post_file(remote_data_file: RemoteDataFile, db: Session = Depends(get_db)):
    try:
        algorithm_name, function_number, dimension, parsed_content = parse_results_file(remote_data_file)
        file = create_file(db, algorithm_name, dimension, function_number, parsed_content)
        # update_rankings([file])
    except IntegrityError:
        raise HTTPException(409, detail='File already exists')
    except ParseError:
        raise HTTPException(422, detail='Unable to parse file')
    return {"data": [4, 3, 2, 10]}
