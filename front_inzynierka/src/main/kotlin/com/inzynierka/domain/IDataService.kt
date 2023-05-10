package com.inzynierka.domain

import com.github.michaelbull.result.Result
import com.inzynierka.data.DomainError
import com.inzynierka.model.Data

interface IDataService {

    suspend fun getData(): Result<Data, DomainError>
}