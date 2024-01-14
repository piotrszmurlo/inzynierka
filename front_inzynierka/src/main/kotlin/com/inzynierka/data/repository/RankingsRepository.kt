package com.inzynierka.data.repository

import com.inzynierka.data.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

class RankingsRepository(private val client: HttpClient) : IRankingsRepository {
    override suspend fun getCec2022Scores(benchmarkName: String): List<ScoreEntryDTO> {
        return client.get(urlString = "rankings/cec2022") {
            parameter("benchmark_name", benchmarkName)
        }.body()
    }

    override suspend fun getFriedmanScores(benchmarkName: String): List<ScoreEntryDTO> {
        return client.get(urlString = "rankings/friedman") {
            parameter("benchmark_name", benchmarkName)
        }.body()
    }

    override suspend fun getStatisticsEntries(benchmarkName: String): List<StatisticsEntryDTO> {
        return client.get(urlString = "rankings/statistics") {
            parameter("benchmark_name", benchmarkName)
        }.body()
    }

    override suspend fun getRevisitedEntries(benchmarkName: String): List<RevisitedEntryDTO> {
        return client.get(urlString = "rankings/revisited") {
            parameter("benchmark_name", benchmarkName)
        }.body()
    }

    override suspend fun getEcdfData(benchmarkName: String): List<EcdfDataDTO> {
        return client.get(urlString = "rankings/ecdf") {
            parameter("benchmark_name", benchmarkName)
        }.body()
    }

    override suspend fun getPairTest(
        firstAlgorithm: String,
        secondAlgorithm: String,
        dimension: Int,
        benchmarkName: String
    ): List<PairTestEntryDTO> {
        return client.submitForm(
            url = "rankings/wilcoxon",
            formParameters = parameters {
                append("first_algorithm", firstAlgorithm)
                append("second_algorithm", secondAlgorithm)
                append("dimension", "$dimension")
                append("benchmark_name", benchmarkName)
            }
        ).body()
    }
}