from fastapi import FastAPI, Depends, HTTPException
from fastapi.middleware.cors import CORSMiddleware
from multipart.exceptions import ParseError
from sqlalchemy.exc import IntegrityError
from sqlalchemy.orm import sessionmaker, Session
from src.dbaccess import create_file, get_file, get_all_files
from python_extensions import parse_results
from src import models
from src.dbaccess import engine, SessionLocal
from src.models import RemoteDataFile
from src.parser import parse_results_file, update_rankings, calculate_cec_ranking, parse_remote_file_name, \
    parse_remote_results_file_v2

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
    print(parse_results("1 2  3   4   5,2,3,4"))
    return {"message": f"Hello {13}"}


@app.get("/rankings")
async def get_rankings(db: Session = Depends(get_db)):
    # calculate_cec_ranking(db)
    medians, averages = update_rankings(get_all_files(db))
    return {
        "average": averages,
        "medians": medians
    }


@app.post("/file")
async def post_file(remote_data_file: RemoteDataFile, db: Session = Depends(get_db)):
    try:
        # print(parse_remote_file_name(remote_data_file.name))
        print(parse_remote_results_file_v2(remote_data_file)[3])
        # file = create_file(db, algorithm_name, dimension, function_number, parsed_content)
        # update_rankings([file])
    except IntegrityError:
        raise HTTPException(409, detail='File already exists')
    except ParseError as e:
        print(e)
        raise HTTPException(422, detail='Unable to parse file')
    return {"data": [4, 3, 2, 10]}
