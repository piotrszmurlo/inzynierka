from src.IUserRepository import IUserRepository


class UserService:
    def __init__(self, repository: IUserRepository):
        self._repository = repository

    def get_user(self, email: str):
        return self._repository.get_user(email)

    def change_password(self, email: str, new_password_hash: str):
        user = self.get_user(email)
        return self._repository.modify_account(user.id, password_hash=new_password_hash)

    def change_email(self, old_email: str, new_email: str):
        user = self.get_user(old_email)
        return self._repository.modify_account(user.id, email=new_email)

    def get_users(self):
        return self._repository.get_users()

    def promote_user_to_admin(self, email):
        return self._repository.promote_user_to_admin(email)

    def verify_user(self, email):
        return self._repository.verify_account(email)

    def create_user(self, email: str, password_hash: str, disabled: bool, is_admin: bool):
        return self._repository.create_user(email, password_hash, disabled, is_admin)
