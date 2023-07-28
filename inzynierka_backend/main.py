import base64
from pprint import pprint

from fastapi import FastAPI, Depends
import python_example as p
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import sessionmaker, Session
from src.dbaccess import create_file, get_file

from src import models
from src.dbaccess import engine, SessionLocal
from src.models import RemoteDataFile
from src.parser import parse_results_file

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


@app.get("/data")
async def get_example_data():
    return {"data": [4, 3, 2, 1]}


@app.post("/file")
async def post_file(remote_data_file: RemoteDataFile, db: Session = Depends(get_db)):
    algorithm_name, function_number, dimension, content = parse_results_file(remote_data_file)
    create_file(db, algorithm_name, dimension, function_number, content)
    return {"data": [4, 3, 2, 10]}
