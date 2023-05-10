package com.example.data

import com.example.domain.IDataService
import com.github.michaelbull.result.Result

class DataService(private val dataRepository: IDataRepository) : IDataService {

    override suspend fun getData(): Result<Data, DomainError> {
        return dataRepository.getData()
    }
}