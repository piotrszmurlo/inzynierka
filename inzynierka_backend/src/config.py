from pydantic_settings import BaseSettings

class Settings(BaseSettings):
    DATABASE_URL: str
    JWT_SECRET_KEY: str
    HASH_ALGORITHM: str
    ORIGINS: list[str]

    class Config:
        env_file = './.env'


settings = Settings()
