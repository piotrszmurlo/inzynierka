package com.inzynierka.data.service

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.data.models.toDomain
import com.inzynierka.data.parsedRemoteExceptionMessage
import com.inzynierka.data.repository.IDataRepository
import com.inzynierka.domain.models.PairTestEntry
import com.inzynierka.domain.models.RevisitedRankingEntry
import com.inzynierka.domain.models.ScoreRankingEntry
import com.inzynierka.domain.models.StatisticsRankingEntry
import com.inzynierka.domain.service.IDataService
import com.inzynierka.model.BenchmarkData
import com.inzynierka.model.EcdfData
import io.kvision.types.KFile

class DataService(private val dataRepository: IDataRepository) : IDataService {

    override suspend fun getAvailableBenchmarkData(): Result<BenchmarkData> {
        return try {
            val algorithms = dataRepository.getAvailableAlgorithms()
            val dimensions = dataRepository.getAvailableDimensions()
            val functionNumbers = dataRepository.getAvailableFunctionNumbers()
            Result.Success(
                BenchmarkData(algorithms, dimensions, functionNumbers)
            )
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getAvailableDimensions(): Result<List<Int>> {
        return try {
            val result = dataRepository.getAvailableDimensions()
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getCec2022Scores(): Result<List<ScoreRankingEntry>> {
        return try {
            val result = dataRepository.getCec2022Scores().map { it.toDomain() }
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getFriedmanScores(): Result<List<ScoreRankingEntry>> {
        return try {
            val result = dataRepository.getFriedmanScores().map { it.toDomain() }
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getStatisticsRankingEntries(): Result<List<StatisticsRankingEntry>> {
        return try {
            val result = dataRepository.getStatisticsEntries().map { it.toDomain() }
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getRevisitedRankingEntries(): Result<List<RevisitedRankingEntry>> {
        return try {
            val result = dataRepository.getRevisitedEntries().map { it.toDomain() }
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getEcdfData(): Result<List<EcdfData>> {
        return try {
            val result = dataRepository.getEcdfData().map { it.toDomain() }
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun postFiles(kFiles: List<KFile>): Result<Unit> {
        return try {
            val result = dataRepository.postFiles(kFiles)
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun getPairTest(
        algorithm1: String,
        algorithm2: String,
        dimension: Int
    ): Result<List<PairTestEntry>> {
        return try {
            val result = dataRepository.getPairTest(algorithm1, algorithm2, dimension).map { it.toDomain() }
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }
}