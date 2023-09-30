package com.inzynierka.data

import com.inzynierka.model.Data
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.kvision.types.KFile

const val API_URL = "http://127.0.0.1:8000"

class DataRepository(private val client: HttpClient) : IDataRepository {
    override suspend fun getData(): Data {
        return client.get("$API_URL/data").body()
    }

    override suspend fun postFile(kFile: KFile): Data {
        TODO("Not yet implemented")
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