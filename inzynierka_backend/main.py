import base64
from pprint import pprint

from fastapi import FastAPI, Depends
import python_example as p
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import sessionmaker, Session

from src import models
from src.dbaccess import engine, SessionLocal, create_file
from src.models import RemoteDataFile

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
    mock_parsed_data = base64.b64decode(remote_data_file.content).decode('utf-8')
    create_file(db, mock_parsed_data)
    pprint(mock_parsed_data)
    return {"data": [4, 3, 2, 1]}
