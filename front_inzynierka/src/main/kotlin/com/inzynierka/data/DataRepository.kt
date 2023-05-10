package com.inzynierka.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import com.inzynierka.model.Data
import io.kvision.rest.RestClient
import io.kvision.rest.call
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
}