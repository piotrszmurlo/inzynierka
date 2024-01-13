package com.inzynierka.data.service

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.data.models.toDomain
import com.inzynierka.data.parsedRemoteExceptionMessage
import com.inzynierka.data.repository.IDataRepository
import com.inzynierka.domain.core.UserData
import com.inzynierka.domain.models.*
import com.inzynierka.domain.service.IDataService
import com.inzynierka.model.BenchmarkData
import com.inzynierka.model.EcdfData
import io.kvision.types.KFile

class DataService(private val dataRepository: IDataRepository) : IDataService {

    override suspend fun getAvailableBenchmarks(): Result<List<Benchmark>> {
        return try {
            Result.Success(dataRepository.getAvailableBenchmarks().map { it.toDomain() })
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getAvailableBenchmarkData(benchmarkName: String): Result<BenchmarkData> {
        return try {
            val algorithms = dataRepository.getAvailableAlgorithms(benchmarkName)
            val dimensions = dataRepository.getAvailableDimensions(benchmarkName)
            val functionNumbers = dataRepository.getAvailableFunctionNumbers(benchmarkName)
            Result.Success(
                BenchmarkData(algorithms, dimensions, functionNumbers)
            )
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getAvailableDimensions(benchmarkName: String): Result<List<Int>> {
        return try {
            Result.Success(dataRepository.getAvailableDimensions(benchmarkName))
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getCec2022Scores(benchmarkName: String): Result<List<ScoreRankingEntry>> {
        return try {
            val result = dataRepository.getCec2022Scores(benchmarkName).map { it.toDomain() }
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getFriedmanScores(benchmarkName: String): Result<List<ScoreRankingEntry>> {
        return try {
            Result.Success(dataRepository.getFriedmanScores(benchmarkName).map { it.toDomain() })
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getStatisticsRankingEntries(benchmarkName: String): Result<List<StatisticsRankingEntry>> {
        return try {
            val result = dataRepository.getStatisticsEntries(benchmarkName).map { it.toDomain() }
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getRevisitedRankingEntries(benchmarkName: String): Result<List<RevisitedRankingEntry>> {
        return try {
            Result.Success(dataRepository.getRevisitedEntries(benchmarkName).map { it.toDomain() })
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getEcdfData(benchmarkName: String): Result<List<EcdfData>> {
        return try {
            Result.Success(dataRepository.getEcdfData(benchmarkName).map { it.toDomain() })
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun postFiles(
        kFiles: List<KFile>,
        benchmarkName: String,
        overwriteExisting: Boolean
    ): Result<Unit> {
        return try {
            Result.Success(dataRepository.postFiles(kFiles, benchmarkName, overwriteExisting))
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun deleteFilesForAlgorithm(algorithmName: String, benchmarkName: String): Result<Unit> {
        return try {
            Result.Success(dataRepository.deleteFilesForAlgorithm(algorithmName, benchmarkName))
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

    override suspend fun promoteUserToAdmin(email: String): Result<Unit> {
        return try {
            Result.Success(dataRepository.promoteUserToAdmin(email))
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

    override suspend fun verifyAccount(code: String): Result<Unit> {
        return try {
            Result.Success(dataRepository.verifyAccount(code))
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun resendVerificationCode(): Result<Unit> {
        return try {
            Result.Success(dataRepository.resendVerificationCode())
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun getUserData(): Result<UserData> {
        return try {
            val data = dataRepository.getUserData()
            Result.Success(
                UserData(disabled = data.disabled, isUserAdmin = data.isAdmin)
            )
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun createBenchmark(
        name: String,
        description: String,
        functionCount: Int,
        trialCount: Int
    ): Result<Unit> {
        return try {
            Result.Success(dataRepository.postBenchmark(name, description, functionCount, trialCount))
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun deleteBenchmark(benchmarkName: String): Result<Unit> {
        return try {
            Result.Success(dataRepository.deleteBenchmark(benchmarkName))
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getPairTest(
        algorithm1: String,
        algorithm2: String,
        dimension: Int,
        benchmarkName: String
    ): Result<List<PairTestEntry>> {
        return try {
            val result =
                dataRepository.getPairTest(algorithm1, algorithm2, dimension, benchmarkName).map { it.toDomain() }
            Result.Success(result)
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun changePassword(newPassword: String, oldPassword: String): Result<Unit> {
        return try {
            Result.Success(dataRepository.changePassword(newPassword, oldPassword))
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun changeEmail(email: String): Result<Unit> {
        return try {
            Result.Success(dataRepository.changeEmail(email))
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }
}
