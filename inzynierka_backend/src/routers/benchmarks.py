from typing import Annotated
from fastapi import APIRouter, Form, HTTPException, Depends
from sqlalchemy.exc import IntegrityError
from src.dependencies.auth import CurrentUserDep, get_file_service, FileServiceDep
from src.services.FileService import FileService

router = APIRouter(prefix='/benchmarks')


@router.get("/")
async def get_all_benchmarks(file_service: FileService = Depends(get_file_service)):
    return file_service.get_benchmarks()


@router.delete("/{benchmark_name}")
async def delete_benchmark(benchmark_name: str, file_service: FileService = FileServiceDep):
    return file_service.delete_benchmark(benchmark_name)


@router.post("/")
async def create_benchmark(name: Annotated[str, Form()], description: Annotated[str, Form()],
                           function_count: Annotated[int, Form()], trial_count: Annotated[int, Form()], file_service: FileService = FileServiceDep):
    try:
        file_service.create_benchmark(name, description, function_count, trial_count)
    except IntegrityError:
        raise HTTPException(409, detail='Benchmark with this name already exists')


@router.get("/{benchmark_name}/algorithms")
async def get_available_algorithms(benchmark_name: str, file_service: FileService = FileServiceDep):
    return file_service.get_algorithm_names(benchmark_name)


@router.get("/{benchmark_name}/algorithms/me")
async def get_available_algorithms(benchmark_name: str, current_user: CurrentUserDep, file_service: FileService = FileServiceDep):
    return file_service.get_algorithm_names(benchmark_name, current_user.id)


@router.get("/{benchmark_name}/dimensions")
async def get_available_dimensions(benchmark_name: str, file_service: FileService = FileServiceDep):
    return file_service.get_dimensions(benchmark_name)


@router.get("/{benchmark_name}/functions")
async def get_available_functions(benchmark_name: str, file_service: FileService = FileServiceDep):
    return file_service.get_function_numbers(benchmark_name)
