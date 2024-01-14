package com.inzynierka.data.service

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.data.models.toDomain
import com.inzynierka.data.parsedRemoteExceptionMessage
import com.inzynierka.data.repository.IBenchmarkRepository
import com.inzynierka.domain.models.Benchmark
import com.inzynierka.domain.service.IBenchmarkService
import com.inzynierka.model.BenchmarkData

class BenchmarkService(val benchmarkRepository: IBenchmarkRepository) : IBenchmarkService {
    override suspend fun createBenchmark(
        name: String,
        description: String,
        functionCount: Int,
        trialCount: Int
    ): Result<Unit> {
        return try {
            Result.Success(benchmarkRepository.postBenchmark(name, description, functionCount, trialCount))
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun deleteBenchmark(benchmarkName: String): Result<Unit> {
        return try {
            Result.Success(benchmarkRepository.deleteBenchmark(benchmarkName))
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getAvailableBenchmarks(): Result<List<Benchmark>> {
        return try {
            Result.Success(benchmarkRepository.getAvailableBenchmarks().map { it.toDomain() })
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getAvailableBenchmarkData(benchmarkName: String): Result<BenchmarkData> {
        return try {
            val algorithms = benchmarkRepository.getAvailableAlgorithms(benchmarkName, false)
            val dimensions = benchmarkRepository.getAvailableDimensions(benchmarkName)
            val functionNumbers = benchmarkRepository.getAvailableFunctionNumbers(benchmarkName)
            Result.Success(
                BenchmarkData(algorithms, dimensions, functionNumbers)
            )
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getAvailableDimensions(benchmarkName: String): Result<List<Int>> {
        return try {
            Result.Success(benchmarkRepository.getAvailableDimensions(benchmarkName))
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun getMyAlgorithms(benchmarkName: String): Result<List<String>> {
        return try {
            Result.Success(benchmarkRepository.getAvailableAlgorithms(benchmarkName, true))
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }
}