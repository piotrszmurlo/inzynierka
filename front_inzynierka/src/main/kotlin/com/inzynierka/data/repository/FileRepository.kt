package com.inzynierka.data.repository

import com.inzynierka.data.actualFileContentOnly
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.kvision.types.KFile

class FileRepository(private val client: HttpClient) : IFileRepository {
    override suspend fun postFiles(kFiles: List<KFile>, benchmarkName: String, overwriteExisting: Boolean) {
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
        ) {
            parameter("overwrite", overwriteExisting)
            parameter("benchmark", benchmarkName)
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }
    }

    override suspend fun deleteFilesForAlgorithm(algorithmName: String, benchmarkName: String) {
        return client.delete(urlString = "file/$algorithmName") {
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
            parameter("benchmark_name", benchmarkName)
        }.body()
    }
}