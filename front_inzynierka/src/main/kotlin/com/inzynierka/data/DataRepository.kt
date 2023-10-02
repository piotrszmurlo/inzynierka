package com.inzynierka.data

import com.inzynierka.model.Data
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
    
    override suspend fun postFiles(kFiles: List<KFile>): Data {
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
        return Data(listOf(69, 1, 69, 1))
    }
}