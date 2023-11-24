from abc import ABC, abstractmethod


class IFileRepository(ABC):

    @abstractmethod
    def get_file(self, algorithm_name: str, dimension: int, function_number: int):
        raise NotImplementedError

    @abstractmethod
    def get_files_for_dimension(self, dimension: int):
        raise NotImplementedError

    @abstractmethod
    def get_files(self):
        raise NotImplementedError

    @abstractmethod
    def get_algorithm_names(self):
        raise NotImplementedError

    @abstractmethod
    def get_dimensions(self):
        raise NotImplementedError

    @abstractmethod
    def get_function_numbers(self):
        raise NotImplementedError

    @abstractmethod
    def create_file(self, algorithm_name: str, dimension: int, function_number: int, content: str):
        raise NotImplementedError

    @abstractmethod
    def delete_files_for_algorithm_name(self, algorithm_name: str):
        raise NotImplementedError
