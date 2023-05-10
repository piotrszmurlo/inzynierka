package com.inzynierka.data

import com.github.michaelbull.result.Result
import com.inzynierka.domain.IDataService
import com.inzynierka.model.Data

class DataService(private val dataRepository: IDataRepository) : IDataService {

    override suspend fun getData(): Result<Data, DomainError> {
        return dataRepository.getData()
    }
}