from src.IUserRepository import IUserRepository


class UserService:
    def __init__(self, repository: IUserRepository):
        self._repository = repository

    def get_user(self, username: str):
        return self._repository.get_user(username)

    def get_users(self):
        return self._repository.get_users()

    def promote_user_to_admin(self, email):
        return self._repository.promote_user_to_admin(email)

    def create_user(self, email: str, password_hash: str, disabled: bool, is_admin: bool):
        return self._repository.create_user(email, password_hash, disabled, is_admin)
