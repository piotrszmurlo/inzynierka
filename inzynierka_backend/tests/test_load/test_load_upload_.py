import base64

from locust import HttpUser, task, between
from locust.exception import RescheduleTask
import urllib.parse

from src.main import ROOT_DIR

BASE_URL = 'http://localhost:8000/'

BENCHMARK_NAME = "test 1"
TEST_DATA_DIR = f"{ROOT_DIR}/tests/test_load/data"

class WebsiteTestUser(HttpUser):
    network_timeout = 30.0
    connection_timeout = 30.0
    wait_time = between(0.5, 3.0)
    @task
    def upload_files(self):
        files = [
            ('files', ('NL-SHADE-RSP-MID_1_10.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_1_10.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_2_10.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_2_10.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_3_10.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_3_10.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_4_10.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_4_10.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_5_10.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_5_10.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_6_10.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_6_10.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_7_10.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_7_10.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_8_10.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_8_10.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_9_10.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_9_10.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_10_10.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_10_10.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_11_10.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_11_10.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_12_10.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_12_10.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_1_20.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_1_20.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_2_20.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_2_20.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_3_20.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_3_20.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_4_20.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_4_20.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_5_20.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_5_20.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_6_20.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_6_20.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_7_20.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_7_20.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_8_20.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_8_20.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_9_20.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_9_20.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_10_20.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_10_20.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_11_20.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_11_20.txt", 'rb').read()), 'multipart/form-data')),
            ('files', ('NL-SHADE-RSP-MID_12_20.txt', base64.b64encode(open(f"{TEST_DATA_DIR}/NL-SHADE-RSP-MID_12_20.txt", 'rb').read()), 'multipart/form-data')),
        ]
        with self.client.post(
            f"{BASE_URL}file/?overwrite=true&benchmark={urllib.parse.quote_plus(BENCHMARK_NAME)}",
            files=files
        ) as r:
            if r.status_code != 200:
                raise RescheduleTask()
