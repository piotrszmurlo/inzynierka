package com.inzynierka.domain.service

import com.github.michaelbull.result.Result
import com.inzynierka.data.DomainError
import com.inzynierka.model.Data
import io.kvision.types.KFile

interface IDataService {

    /**
     * gets mock data for chart
     * @return Result containing the data, or DomainError if an error occurred
     **/
    suspend fun getData(): Result<Data, DomainError>

    /**
     * uploads a KFile to the server
     * @return Result containing mock data, or DomainError if an error occurred
     **/
    suspend fun postFile(kFile: KFile): Result<Data, DomainError>
}