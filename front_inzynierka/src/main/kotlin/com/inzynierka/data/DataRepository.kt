package com.inzynierka.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.inzynierka.model.Data
import io.kvision.rest.HttpMethod
import io.kvision.rest.RestClient
import io.kvision.rest.call
import io.kvision.rest.post
import io.kvision.types.KFile
import kotlinx.coroutines.await


class DataRepository(private val restClient: RestClient) : IDataRepository {
    override suspend fun getData(): Result<Data, DomainError> {
        var result: Result<Data, DomainError> = Err(DomainError.NetworkError("Unknown error"))
        restClient.call<Data>("http://127.0.0.1:8000/data")
            .then { result = Ok(it) }
            .catch { result = Err(DomainError.NetworkError(it.message)) }
            .await()
        return result
    }

    override suspend fun postFile(kFile: KFile): Result<Unit, DomainError> {
        var result: Result<Unit, DomainError> = Err(DomainError.NetworkError("Unknown error"))
        restClient.post<String, String>(
            url = "http://127.0.0.1:8000/file",
            data = kFile.content!!,
        ) {
            method = HttpMethod.POST
            contentType = "multipart/form-data"
        }.then { result = Ok(Unit) }
            .catch { result = Err(DomainError.NetworkError(it.message)) }
            .await()
        return result
    }
}