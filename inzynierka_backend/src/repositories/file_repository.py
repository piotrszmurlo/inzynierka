from abc import ABC, abstractmethod
from typing import List

from src.models.benchmark import Benchmark


class IFileRepository(ABC):

    @abstractmethod
    def get_file(self, algorithm_name: str, dimension: int, function_number: int, benchmark_name: str):
        raise NotImplementedError

    @abstractmethod
    def get_files_for_dimension(self, dimension: int, benchmark_name: str):
        raise NotImplementedError

    @abstractmethod
    def get_benchmarks(self) -> List[Benchmark]:
        raise NotImplementedError

    @abstractmethod
    def create_benchmark(self, name: str, description: str, function_count: int, trial_count: int):
        raise NotImplementedError

    @abstractmethod
    def get_benchmark(self, benchmark_name: str):
        raise NotImplementedError

    @abstractmethod
    def get_files(self, benchmark_name: str):
        raise NotImplementedError

    @abstractmethod
    def get_algorithm_names(self, benchmark_name: str):
        raise NotImplementedError

    @abstractmethod
    def get_algorithm_names_for_user(self, benchmark_name: str, user_id: int):
        raise NotImplementedError


    @abstractmethod
    def get_dimensions(self, benchmark_name: str):
        raise NotImplementedError

    @abstractmethod
    def get_function_numbers(self, benchmark_name: str):
        raise NotImplementedError

    @abstractmethod
    def get_files_for_algorithm_name(self, algorithm_name: str, benchmark_name: str):
        raise NotImplementedError

    @abstractmethod
    def create_file(self, algorithm_name: str, dimension: int, function_number: int, content: str, benchmark_id: int, owner_id: int):
        raise NotImplementedError

    @abstractmethod
    def delete_files_for_algorithm_name(self, algorithm_name: str, benchmark_name: str):
        raise NotImplementedError

    @abstractmethod
    def delete_benchmark(self, benchmark_name: str):
        raise NotImplementedError

