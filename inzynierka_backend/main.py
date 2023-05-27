import base64
from pprint import pprint

from fastapi import FastAPI
import python_example as p
from fastapi.middleware.cors import CORSMiddleware

from src.models import RemoteDataFile

app = FastAPI()

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
async def post_file(remote_data_file: RemoteDataFile):
    decoded_data = base64.b64decode(remote_data_file.content).decode('utf-8').split("  ")
    pprint(decoded_data)
    return {"data": [4, 3, 2, 1]}
