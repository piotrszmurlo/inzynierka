package com.inzynierka.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.inzynierka.model.Data
import io.kvision.rest.RestClient
import io.kvision.rest.call
import io.kvision.rest.post
import io.kvision.types.KFile
import kotlinx.coroutines.await

const val API_URL = "http://127.0.0.1:8000"

class DataRepository(private val restClient: RestClient) : IDataRepository {
    override suspend fun getData(): Result<Data, DomainError> {
        var result: Result<Data, DomainError> = Err(DomainError.NetworkError("Unknown error"))
        restClient.call<Data>("$API_URL/data")
            .then { result = Ok(it) }
            .catch { result = Err(DomainError.NetworkError(it.message)) }
            .await()
        return result
    }

    override suspend fun postFile(kFile: KFile): Result<Unit, DomainError> {
        val kFileContentOnly = KFile(kFile.name, kFile.size, kFile.actualFileContentOnly)
        console.log(kFile.actualFileContentOnly)
        var result: Result<Unit, DomainError> = Err(DomainError.NetworkError("Unknown error"))
        restClient.post<String, KFile>("$API_URL/file", kFileContentOnly)
            .then { result = Ok(Unit) }
            .catch { result = Err(DomainError.NetworkError(it.message)) }
            .await()
        return result
    }
}