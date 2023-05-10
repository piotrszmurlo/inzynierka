package com.example.data

import com.github.michaelbull.result.Result

interface IDataRepository {
    suspend fun getData() : Result<Data, DomainError>
}