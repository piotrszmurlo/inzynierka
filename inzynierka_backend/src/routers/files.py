from fastapi import APIRouter, HTTPException, UploadFile
from sqlalchemy.exc import IntegrityError
from starlette import status
from src.dependencies.auth import CurrentActiveUserDep
from src.dependencies.auth import FileServiceDep
from src.models.parse_error import ParseError
from src.dependencies.parser import ALL_DIMENSIONS, check_filenames_integrity, parse_remote_filename, parse_remote_results_file
from src.services.FileService import FileService

router = APIRouter()


@router.delete("/file")
async def delete_files(algorithm_name: str, benchmark_name: str, current_user: CurrentActiveUserDep, file_service: FileService = FileServiceDep):
    files = file_service.get_files_for_algorithm_name(algorithm_name, benchmark_name)

    for file in files:
        if file.owner_id != current_user.id and not current_user.is_admin:
            raise HTTPException(
                status_code=status.HTTP_403_FORBIDDEN,
                detail="Current user does not have permission to perform this action"
            )
    file_service.delete_files(algorithm_name, benchmark_name)


@router.post("/file")
async def post_file(files: list[UploadFile], benchmark: str, overwrite: bool, current_user: CurrentActiveUserDep, file_service: FileService = FileServiceDep):
    try:
        benchmark_data = file_service.get_benchmark(benchmark)
        if not benchmark_data:
            raise HTTPException(404, detail='Given benchmark does not exist')
        if len(files) != benchmark_data.function_count * len(ALL_DIMENSIONS):
            raise ParseError(
                f"Provide exactly {benchmark_data.function_count * len(ALL_DIMENSIONS)} files, one for each function-dimension pair"
            )
        check_filenames_integrity(
            [parse_remote_filename(file.filename) for file in files],
            benchmark_data.function_count
        )
        parsed_file_tuples = []
        for file in files:
            parsed_file_tuples.append(
                parse_remote_results_file(
                    file.filename, await file.read(), benchmark_data.trial_count
                )
            )
        if overwrite:
            existing_file = file_service.get_file(algorithm_name=parsed_file_tuples[0][0], function_number=parsed_file_tuples[0][1], dimension=parsed_file_tuples[0][2], benchmark_name=benchmark)
            if existing_file:
                if existing_file.owner_id != current_user.id and not current_user.is_admin:
                    raise HTTPException(
                        status_code=status.HTTP_403_FORBIDDEN,
                        detail="Current user does not have permission to perform this action"
                    )
                file_service.delete_files(parsed_file_tuples[0][0], benchmark_data.name)
        for algorithm_name, function_number, dimension, content in parsed_file_tuples:
            file_service.create_file(algorithm_name=algorithm_name, function_number=function_number,
                                     dimension=dimension, content=content, benchmark_id=benchmark_data.id, owner_id=current_user.id)
    except IntegrityError:
        raise HTTPException(409, detail='File already exists')
    except ParseError as e:
        raise HTTPException(422, detail=str(e))
    except UnicodeDecodeError as e:
        raise HTTPException(422, detail=str(e))
