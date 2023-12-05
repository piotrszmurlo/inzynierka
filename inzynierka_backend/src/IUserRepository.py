from abc import ABC, abstractmethod


class IUserRepository(ABC):

    @abstractmethod
    def get_user(self, username: str):
        raise NotImplementedError

    @abstractmethod
    def get_users(self):
        raise NotImplementedError

    @abstractmethod
    def promote_user_to_admin(self, email):
        raise NotImplementedError

    @abstractmethod
    def create_user(self, email: str, password_hash: str, disabled: bool, is_admin: bool):
        raise NotImplementedError
