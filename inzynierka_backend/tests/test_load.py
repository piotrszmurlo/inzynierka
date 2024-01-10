from locust import HttpUser, task, between
from locust.exception import RescheduleTask
import urllib.parse

BASE_URL = 'http://localhost:8000/'

BENCHMARK_NAME = "test 1"


class WebsiteTestUser(HttpUser):
    network_timeout = 30.0
    connection_timeout = 30.0
    wait_time = between(0.5, 3.0)

    @task
    def cec_2022(self):
        self.get_ranking("cec2022", BENCHMARK_NAME)

    @task
    def ecdf(self):
        self.get_ranking("ecdf", BENCHMARK_NAME)

    @task
    def statistics(self):
        self.get_ranking("statistics", BENCHMARK_NAME)

    @task
    def friedman(self):
        self.get_ranking("friedman", BENCHMARK_NAME)

    @task
    def revisited(self):
        self.get_ranking("revisited", BENCHMARK_NAME)

    def get_ranking(self, ranking: str, benchmark: str):
        with self.client.get(f"{BASE_URL}rankings/{ranking}?benchmark_name={urllib.parse.quote_plus(benchmark)}") as r:
            if r.status_code != 200:
                raise RescheduleTask()
