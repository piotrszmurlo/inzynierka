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
            Result.Success(dataRepository.getAvailableDimensions())
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
            Result.Success(dataRepository.getFriedmanScores().map { it.toDomain() })
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
            Result.Success(dataRepository.getRevisitedEntries().map { it.toDomain() })
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getEcdfData(): Result<List<EcdfData>> {
        return try {
            Result.Success(dataRepository.getEcdfData().map { it.toDomain() })
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun postFiles(kFiles: List<KFile>): Result<Unit> {
        return try {
            Result.Success(dataRepository.postFiles(kFiles))
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun deleteFilesForAlgorithm(algorithmName: String): Result<Unit> {
        return try {
            Result.Success(dataRepository.deleteFilesForAlgorithm(algorithmName))
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            Result.Success(dataRepository.loginUser(email, password))
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun registerUser(email: String, password: String): Result<Unit> {
        return try {
            Result.Success(dataRepository.registerUser(email, password))
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun isCurrentUserAdmin(): Result<Boolean> {
        return try {
            Result.Success(dataRepository.isCurrentUserAdmin().isAdmin)
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