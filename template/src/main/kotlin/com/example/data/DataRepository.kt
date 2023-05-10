package com.example.data

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import io.kvision.rest.RestClient
import io.kvision.rest.call
import kotlinx.coroutines.await
import kotlinx.serialization.Serializable

@Serializable
data class Data(val data: List<Int>)

class DataRepository : IDataRepository {
    override suspend fun getData(): Result<Data, DomainError> {
        val restClient = RestClient()
        var result: Result<Data, DomainError> = Err(DomainError.NetworkError("Unknown error"))
        restClient.call<Data>("http://127.0.0.1:8000/data")
            .then { result = Ok(it) }
            .catch { result = Err(DomainError.NetworkError(it.message!!)) }
            .await()
        return result
    }
}