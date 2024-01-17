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

`.env` also contains other setup, such as for SMTP server.

Test SMTP server (necessary for verifying accounts):

`python3 -m smtpd -c DebuggingServer -n localhost:1025`

To run backend tests (python and c++ tests), run `./run_tests.sh` while in inzynierka_backend folder:

To run frontend tests (kotlin tests), run `./gradlew test`` while in front_inzynierka folder:

Generated coverage report path: /inzynierka_backend/htmlcov/index.html

Building frontend app for production:

Change API_URL constant in file `front_inzynierka/src/main/kotlin/com/inzynierka/Config.kt` to appropariate IP address and port, then:

`./gradlew clean zip`

The package containing all of application files (index.html + main.bundle.js) will be saved as build/libs/template-1.0.0-SNAPSHOT.zip. 

Website can be hosted using e.g. Apache 2.4 (Ubuntu):

`sudo apt update`

`sudo apt install apache2`

`sudo mkdir /var/www/aecomparison/`

`cd /etc/apache2/sites-available/`

`sudo cp 000-default.conf aecomparison.conf`

`sudo nano aecomparison.conf`


        <VirtualHost *:80>
                ServerName <domain> [or localhost for testing]
        
                DocumentRoot /var/www/aecomparison
        
                ErrorLog ${APACHE_LOG_DIR}/error.log
                CustomLog ${APACHE_LOG_DIR}/access.log combined
        </VirtualHost>

`sudo service apache2 reload`

`sudo service apache2 restart`

Move `index.html` and `main.bundle.js` into `/var/www/aecomparison/`

Run backend app for production (using uvicorn: `pip install "uvicorn[standard]"`):

`uvicorn src.main:app --host 0.0.0.0 --port 8000`

