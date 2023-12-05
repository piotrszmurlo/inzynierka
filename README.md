Frontend server requirements: JDK 17+ required

Backend server requirements: Python 3.9 + pipenv (`pip install pipenv`)

To run frontend server, run `./gradlew run` while in front_inzynierka folder and access via `localhost:3000`

To run backend server, run while in inzynierka_backend folder:

`python3 -m pipenv install`

`python3 -m pipenv sync`

`python3 -m pipenv run install`

`pipenv [...]` instead of `python3 -m pipenv [...]` if installed globally

modify database connection by changing DATABASE_URL in `.env` file

`dialect+driver://username:password@host:port/database`

Test SMTP server (necessary for verifying accounts):

`python3 -m smtpd -c DebuggingServer -n localhost:1025`

To run backend tests (python and c++ tests), run `./run_tests.sh` while in inzynierka_backend folder:

Generated coverage report path: /inzynierka_backend/htmlcov/index.html

Building frontend app for production:

`./gradlew clean zip`

The package containing all of application files (index.html + main.bundle.js) will be saved as build/libs/template-1.0.0-SNAPSHOT.zip.
