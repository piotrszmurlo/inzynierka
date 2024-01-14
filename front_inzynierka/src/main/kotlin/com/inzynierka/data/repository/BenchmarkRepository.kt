package com.inzynierka.data.repository

import com.inzynierka.data.models.BenchmarkDTO
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class BenchmarkRepository(private val client: HttpClient) : IBenchmarkRepository {
    override suspend fun getAvailableAlgorithms(benchmarkName: String): List<String> {
        return client.get(urlString = "/benchmarks/$benchmarkName/algorithms").body()
    }

    override suspend fun getAvailableDimensions(benchmarkName: String): List<Int> {
        return client.get(urlString = "/benchmarks/$benchmarkName/dimensions").body()
    }

    override suspend fun getAvailableFunctionNumbers(benchmarkName: String): List<Int> {
        return client.get(urlString = "/benchmarks/$benchmarkName/functions").body()
    }


    override suspend fun getAvailableBenchmarks(): List<BenchmarkDTO> {
        return client.get(urlString = "/benchmarks/").body()
    }


    override suspend fun postBenchmark(name: String, description: String, functionCount: Int, trialCount: Int) {
        client.submitForm(
            url = "benchmarks",
            formParameters = Parameters.build {
                this.append("name", name)
                this.append("description", description)
                this.append("function_count", functionCount.toString())
                this.append("trial_count", trialCount.toString())
            }
        ) {
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }
    }

    override suspend fun deleteBenchmark(benchmarkName: String) {
        client.delete(urlString = "benchmarks/$benchmarkName") {
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }
    }
}