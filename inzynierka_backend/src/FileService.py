from src import IFileRepository


class FileService:
    def __init__(self, repository: IFileRepository):
        self._repository = repository

    def get_file(self, algorithm_name: str, dimension: int, function_number: int):
        return self._repository.get_file(algorithm_name, dimension, function_number)

    def get_files_for_dimension(self, dimension: int):
        return self._repository.get_files_for_dimension(dimension)

    def get_files(self):
        return self._repository.get_files()

    def get_algorithm_names(self):
        return self._repository.get_algorithm_names()

    def get_dimensions(self):
        return self._repository.get_dimensions()

    def get_function_numbers(self):
        return self._repository.get_function_numbers()

    def create_file(self, algorithm_name: str, dimension: int, function_number: int, content: str):
        return self._repository.create_file(
            algorithm_name=algorithm_name,
            contents=content,
            dimension=dimension,
            function_number=function_number
        )

