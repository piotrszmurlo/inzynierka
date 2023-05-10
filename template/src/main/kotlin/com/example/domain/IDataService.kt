package com.example.domain

import com.example.data.DomainError
import com.example.data.Data
import com.github.michaelbull.result.Result

interface IDataService {

    suspend fun getData(): Result<Data, DomainError>
}