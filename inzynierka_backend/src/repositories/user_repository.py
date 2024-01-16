from abc import ABC, abstractmethod
from typing import Optional


class IUserRepository(ABC):

    @abstractmethod
    def get_user(self, email: str):
        raise NotImplementedError

    @abstractmethod
    def get_users(self):
        raise NotImplementedError

    @abstractmethod
    def promote_user_to_admin(self, email):
        raise NotImplementedError

    @abstractmethod
    def verify_account(self, email):
        raise NotImplementedError

    @abstractmethod
    def modify_account(self, user_id: int, **kwargs):
        raise NotImplementedError

    @abstractmethod
    def create_user(self, email: str, password_hash: str, verification_hash: str, disabled: bool, is_admin: bool):
        raise NotImplementedError
