package com.inzynierka.data

import com.inzynierka.model.RankingScores
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.kvision.types.KFile


class DataRepository(private val client: HttpClient) : IDataRepository {

    override suspend fun getCec2022Scores(): RankingScores {
        return client.get(urlString = "rankings/cec2022").body()
    }

    override suspend fun getFriedmanScores(): RankingScores {
        return client.get(urlString = "rankings/friedman").body()
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
        dimension: Int,
        functionNumber: Int
    ): String {
        return client.submitForm(
            url = "rankings/wilcoxon",
            formParameters = parameters {
                append("first_algorithm", firstAlgorithm)
                append("second_algorithm", secondAlgorithm)
                append("dimension", "$dimension")
                append("function_number", "$functionNumber")
            }
        ).body()
    }
}