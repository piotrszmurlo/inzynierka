package com.inzynierka.data

import com.inzynierka.model.Data
import com.inzynierka.model.RemoteCEC2022Data
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.kvision.types.KFile


class DataRepository(private val client: HttpClient) : IDataRepository {

    override suspend fun getData(): Data {
        return client.get(urlString = "data").body()
    }

    override suspend fun getCEC2022Scores(): RemoteCEC2022Data {
        return client.get(urlString = "rankings/cec2022").body()
    }

    override suspend fun getAvailableAlgorithms(): List<String> {
        return client.get(urlString = "algorithms").body()
    }

    override suspend fun getAvailableDimensions(): List<Int> {
        return listOf(1, 2)
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
}