import pytest
from starlette.testclient import TestClient

from src.dependencies.auth import get_file_service
from src.main import app
from unittest.mock import MagicMock, Mock

from src.models.benchmark import Benchmark
from src.services.FileService import FileService

client = TestClient(app)


def mock_get_file_service():
    mock_benchmarks = [Benchmark(id=3, name="test_benchmark", description="test description", trial_count =30, function_count=12)]
    mock = Mock()

    attrs = {'get_benchmarks.return_value': mock_benchmarks}
    mock.configure_mock(**attrs)
    a = FileService(mock)
    return a


app.dependency_overrides[get_file_service] = mock_get_file_service


def test_read_main():
    expected = [
        {'description': 'test description',
          'function_count': 12,
          'id': 3,
          'name': 'test_benchmark',
          'trial_count': 30
         }
                ]
    response = client.get("/benchmarks")
    assert response.status_code == 200
    assert response.json() == expected
