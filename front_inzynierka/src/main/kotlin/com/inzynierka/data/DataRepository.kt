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

    override suspend fun postFile(kFile: KFile): Data {
        client.submitFormWithBinaryData(
            url = "file",
            formData = formData {
                append("file", kFile.actualFileContentOnly!!, Headers.build {
                    append(HttpHeaders.ContentType, "multipart/form-data")
                    append(HttpHeaders.ContentDisposition, "filename=\"${kFile.name}\"")
                })
            }
        )
        return Data(listOf(69, 1, 69, 1))
//        val kFileContentOnly = KFile(kFile.name, kFile.size, kFile.actualFileContentOnly)
//        var result: Result<Data, DomainError> = Err(DomainError.NetworkError("Unknown error"))
//        client.post<Data, KFile>("$API_URL/file", kFileContentOnly)
//            .then { result = Ok(it) }
//            .catch { result = Err(DomainError.NetworkError(it.message)) }
//            .await()
//        return result
    }

    override suspend fun postFiles(kFiles: List<KFile>): Data {
        TODO("Not yet implemented")
    }
}