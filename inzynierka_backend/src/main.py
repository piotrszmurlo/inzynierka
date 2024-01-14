import os

from fastapi import FastAPI
from fastapi.middleware.cors import CORSMiddleware

from src.models.base import Base
from src.config import settings
from src.repositories_impl.SQLAlchemyFileRepository import engine
from src.routers import users, benchmarks, files, rankings

Base.metadata.create_all(bind=engine)
app = FastAPI()
ROOT_DIR = os.path.dirname(os.path.abspath(__file__))


app.add_middleware(
    CORSMiddleware,
    allow_origins=settings.ORIGINS,
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

app.include_router(users.router)
app.include_router(benchmarks.router)
app.include_router(files.router)
app.include_router(rankings.router)

