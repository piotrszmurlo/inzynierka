package com.inzynierka.data.repository

import com.inzynierka.data.models.BenchmarkDTO

interface IBenchmarkRepository {
    suspend fun getAvailableAlgorithms(benchmarkName: String, ownedOnly: Boolean): List<String>
    suspend fun getAvailableDimensions(benchmarkName: String): List<Int>
    suspend fun getAvailableFunctionNumbers(benchmarkName: String): List<Int>

    suspend fun postBenchmark(name: String, description: String, functionCount: Int, trialCount: Int)
    suspend fun deleteBenchmark(benchmarkName: String)

    suspend fun getAvailableBenchmarks(): List<BenchmarkDTO>
}