package com.inzynierka.data.repository

import com.inzynierka.data.actualFileContentOnly
import com.inzynierka.data.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.kvision.types.KFile


class DataRepository(private val client: HttpClient) : IDataRepository {

    override suspend fun getCec2022Scores(): List<ScoreEntryDTO> {
        return client.get(urlString = "rankings/cec2022").body()
    }

    override suspend fun getFriedmanScores(): List<ScoreEntryDTO> {
        return client.get(urlString = "rankings/friedman").body()
    }

    override suspend fun getStatisticsEntries(): List<StatisticsEntryDTO> {
        return client.get(urlString = "rankings/statistics").body()
    }

    override suspend fun getRevisitedEntries(): List<RevisitedEntryDTO> {
        return client.get(urlString = "rankings/revisited").body()
    }

    override suspend fun getEcdfData(): List<EcdfDataDTO> {
        return client.get(urlString = "rankings/ecdf").body()
    }

    override suspend fun getAvailableAlgorithms(): List<String> {
        return client.get(urlString = "algorithms").body()
    }

    override suspend fun getAvailableDimensions(): List<Int> {
        return client.get(urlString = "dimensions").body()
    }

    override suspend fun getAvailableFunctionNumbers(): List<Int> {
        return client.get(urlString = "functions").body()
    }

    override suspend fun deleteFilesForAlgorithm(algorithmName: String) {
        return client.delete(urlString = "file/$algorithmName").body()
    }

    override suspend fun postFiles(kFiles: List<KFile>) {
        client.submitFormWithBinaryData(
            url = "file",
            formData = formData {
                kFiles.forEach { file ->
                    this.append("files", file.actualFileContentOnly!!, Headers.build {
                        append(HttpHeaders.ContentType, "multipart/form-data")
                        append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                    })
                }
            }
        )
    }

    override suspend fun getPairTest(
        firstAlgorithm: String,
        secondAlgorithm: String,
        dimension: Int
    ): List<PairTestEntryDTO> {
        return client.submitForm(
            url = "rankings/wilcoxon",
            formParameters = parameters {
                append("first_algorithm", firstAlgorithm)
                append("second_algorithm", secondAlgorithm)
                append("dimension", "$dimension")
            }
        ).body()
    }
}