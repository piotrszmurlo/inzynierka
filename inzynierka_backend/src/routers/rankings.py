from typing import Annotated

from fastapi import APIRouter, Form, HTTPException

from src.dependencies.auth import FileServiceDep, RankingsServiceDep
from src.dependencies.rankings_service import RankingsService
from src.services.FileService import FileService

router = APIRouter(prefix="/rankings")


@router.get("/cec2022")
async def get_cec2022_ranking(benchmark_name: str, file_service: FileService = FileServiceDep, rankings: RankingsService = RankingsServiceDep):
    benchmark = file_service.get_benchmark(benchmark_name)
    return rankings.get_cec2022_ranking_scores(benchmark)


@router.get("/friedman")
async def get_friedman_ranking(benchmark_name: str, file_service: FileService = FileServiceDep, rankings: RankingsService = RankingsServiceDep):
    benchmark = file_service.get_benchmark(benchmark_name)
    return rankings.get_friedman_ranking_scores(benchmark)


@router.get("/statistics")
async def get_statistics_ranking_data(benchmark_name: str, file_service: FileService = FileServiceDep, rankings: RankingsService = RankingsServiceDep):
    benchmark = file_service.get_benchmark(benchmark_name)
    return rankings.get_statistics_ranking_data(benchmark)


@router.get("/revisited")
async def get_revisited_ranking(benchmark_name: str, file_service: FileService = FileServiceDep, rankings: RankingsService = RankingsServiceDep):
    benchmark = file_service.get_benchmark(benchmark_name)
    return rankings.get_revisited_ranking_entries(benchmark)


@router.get("/ecdf")
async def get_ecdf_data(benchmark_name: str, file_service: FileService = FileServiceDep, rankings: RankingsService = RankingsServiceDep):
    benchmark = file_service.get_benchmark(benchmark_name)
    return rankings.get_ecdf_data(benchmark)


@router.post("/wilcoxon")
async def get_wilcoxon_test(
        benchmark_name: Annotated[str, Form()],
        first_algorithm: Annotated[str, Form()],
        second_algorithm: Annotated[str, Form()],
        dimension: Annotated[int, Form()],
        file_service: FileService = FileServiceDep,
        rankings: RankingsService = RankingsServiceDep
):
    try:
        benchmark = file_service.get_benchmark(benchmark_name)
        return rankings.get_wilcoxon_test(
            first_algorithm=first_algorithm,
            second_algorithm=second_algorithm,
            dimension=dimension,
            benchmark=benchmark
        )
    except ValueError as e:
        raise HTTPException(422, detail=str(e))
