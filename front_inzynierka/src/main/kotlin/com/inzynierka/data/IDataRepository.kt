package com.inzynierka.data

import com.github.michaelbull.result.Result
import com.inzynierka.model.Data

interface IDataRepository {
    suspend fun getData(): Result<Data, DomainError>
}