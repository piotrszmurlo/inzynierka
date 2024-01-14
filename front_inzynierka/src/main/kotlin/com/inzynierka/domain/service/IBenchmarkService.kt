package com.inzynierka.domain.service

import com.inzynierka.common.Result
import com.inzynierka.domain.models.Benchmark
import com.inzynierka.model.BenchmarkData

interface IBenchmarkService {

    suspend fun createBenchmark(name: String, description: String, functionCount: Int, trialCount: Int): Result<Unit>

    suspend fun deleteBenchmark(benchmarkName: String): Result<Unit>
    suspend fun getAvailableBenchmarks(): Result<List<Benchmark>>
    suspend fun getAvailableBenchmarkData(benchmarkName: String): Result<BenchmarkData>
    suspend fun getAvailableDimensions(benchmarkName: String): Result<List<Int>>
}