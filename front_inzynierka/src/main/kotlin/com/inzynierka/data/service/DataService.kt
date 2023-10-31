package com.inzynierka.data.service

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.data.models.toDomain
import com.inzynierka.data.repository.IDataRepository
import com.inzynierka.domain.models.ScoreRankingEntry
import com.inzynierka.domain.models.StatisticsRankingEntry
import com.inzynierka.domain.service.IDataService
import com.inzynierka.model.BenchmarkData
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
        } catch (e: Exception) {
            Result.Error(DomainError.NetworkError(e.message))
        }
    }

    override suspend fun getAvailableDimensions(): Result<List<Int>> {
        return try {
            val result = dataRepository.getAvailableDimensions()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(DomainError.NetworkError(e.message))
        }
    }

    override suspend fun getCec2022Scores(): Result<List<ScoreRankingEntry>> {
        return try {
            val result = dataRepository.getCec2022Scores().map { it.toDomain() }
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(DomainError.NetworkError(e.message))
        }
    }

    override suspend fun getFriedmanScores(): Result<List<ScoreRankingEntry>> {
        return try {
            val result = dataRepository.getFriedmanScores().map { it.toDomain() }
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(DomainError.NetworkError(e.message))
        }
    }

    override suspend fun getStatisticsRankingEntries(): Result<List<StatisticsRankingEntry>> {
        return try {
            val result = dataRepository.getStatisticsEntries().map { it.toDomain() }
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(DomainError.NetworkError(e.message))
        }
    }

    override suspend fun postFiles(kFiles: List<KFile>): Result<Unit> {
        return try {
            val result = dataRepository.postFiles(kFiles)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(DomainError.FileUploadError(e.message))
        }
    }

    override suspend fun getPairTest(
        algorithm1: String,
        algorithm2: String,
        dimension: Int,
        functionNumber: Int
    ): Result<String> {
        return try {
            val result = dataRepository.getPairTest(algorithm1, algorithm2, dimension, functionNumber)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(DomainError.NetworkError(e.message))
        }
    }
}