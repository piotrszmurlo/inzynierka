from src.models.file import LocalFile
from src.repositories import IFileRepository


class FileService:
    def __init__(self, repository: IFileRepository):
        self._repository = repository

    def get_file(self, algorithm_name: str, dimension: int, function_number: int, benchmark_name: str):
        return self._repository.get_file(algorithm_name, dimension, function_number, benchmark_name)

    def get_files_for_dimension(self, dimension: int, benchmark_name: str):
        return self._repository.get_files_for_dimension(dimension, benchmark_name)

    def get_benchmarks(self):
        return self._repository.get_benchmarks()

    def get_benchmark(self, benchmark_name: str):
        return self._repository.get_benchmark(benchmark_name)

    def create_benchmark(self, name: str, description: str, function_count: int, trial_count: int):
        return self._repository.create_benchmark(name, description, function_count, trial_count)

    def get_files(self, benchmark_name: str):
        return self._repository.get_files(benchmark_name)

    def get_algorithm_names(self, benchmark_name: str):
        return self._repository.get_algorithm_names(benchmark_name)

    def get_dimensions(self, benchmark_name: str):
        return self._repository.get_dimensions(benchmark_name)

    def get_function_numbers(self, benchmark_name: str):
        return self._repository.get_function_numbers(benchmark_name)

    def create_file(self, algorithm_name: str, dimension: int, function_number: int, content: str, benchmark_id: int, owner_id: int):
        return self._repository.create_file(
            algorithm_name=algorithm_name,
            content=content,
            dimension=dimension,
            function_number=function_number,
            benchmark_id=benchmark_id,
            owner_id=owner_id
        )

    def delete_files(self, algorithm_name: str, benchmark_name: str):
        return self._repository.delete_files_for_algorithm_name(algorithm_name, benchmark_name)

    def get_files_for_algorithm_name(self, algorithm_name: str, benchmark_name: str) -> list[LocalFile]:
        return self._repository.get_files_for_algorithm_name(algorithm_name, benchmark_name)

    def delete_benchmark(self, benchmark_name: str):
        return self._repository.delete_benchmark(benchmark_name)
