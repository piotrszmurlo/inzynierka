Frontend server requirements: JDK 17+ required

Backend server requirements: Python 3.9 + pipenv (`pip install pipenv`)

To run frontend server, run `./gradlew run` while in front_inzynierka folder and access via `localhost:3000`

To run backend server, run while in inzynierka_backend folder:

`python3 -m pipenv install`

`python3 -m pipenv sync`

`python3 -m pipenv run install`

`pipenv [...]` instead of `python3 -m pipenv [...]` if installed globally

modify database connection by changing SQLALCHEMY_DATABASE_URL in SQLAlchemyFileRepository.py::9

`dialect+driver://username:password@host:port/database`
