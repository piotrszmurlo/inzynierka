package com.inzynierka.data

import com.github.michaelbull.result.Result
import com.inzynierka.domain.service.IDataService
import com.inzynierka.model.Data
import io.kvision.types.KFile

class DataService(private val dataRepository: IDataRepository) : IDataService {

    override suspend fun getData(): Result<Data, DomainError> {
        return dataRepository.getData()
    }

    override suspend fun postFile(kFile: KFile): Result<Unit, DomainError> {
        return dataRepository.postFile(kFile)
    }
}