from sqlalchemy import create_engine
from sqlalchemy.orm import Session, sessionmaker

from src import models

SQLALCHEMY_DATABASE_URL = "mysql+pymysql://root:inzynierka123@localhost:3306/inzynierka"

engine = create_engine(SQLALCHEMY_DATABASE_URL)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

def get_file(db: Session, file_id: int):
    return db.query(models.LocalFile).filter(models.LocalFile.id == file_id).first()


def create_file(db: Session, content: str):
    db_file = models.LocalFile(contents=content)
    db.add(db_file)
    db.commit()
    db.refresh(db_file)
    return db_file
