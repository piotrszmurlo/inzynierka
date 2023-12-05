from sqlalchemy.orm import Session
from sqlalchemy import select, update

from src import models
from src.IUserRepository import IUserRepository


class SQLAlchemyUserRepository(IUserRepository):

    def __init__(self, db: Session):
        self._db = db

    def get_user(self, email: str):
        query = select(models.UserTable).filter_by(email=email)
        return self._db.scalars(query).first()

    def get_users(self):
        query = select(models.UserTable)
        return self._db.scalars(query).all()

    def promote_user_to_admin(self, email):
        query = update(models.UserTable).where(models.UserTable.email == email).values(is_admin = True)
        self._db.execute(query)
        self._db.commit()

    def create_user(self, email: str, password_hash: str, disabled: bool, is_admin: bool):
        try:
            file = models.UserTable(
                email=email,
                password_hash=password_hash,
                disabled=disabled,
                is_admin=is_admin
            )
            self._db.add(file)
            self._db.commit()
        except:
            self._db.rollback()
            raise
