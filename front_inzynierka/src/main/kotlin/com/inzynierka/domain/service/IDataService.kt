package com.inzynierka.domain.service

import com.inzynierka.domain.Result
import com.inzynierka.model.Data
import io.kvision.types.KFile

interface IDataService {

    /**
     * gets mock data for chart
     * @return [Result] containing the data, or DomainError if an error occurred
     **/
    suspend fun getData(): Result<Data>

    /**
     * uploads [kFiles] to the server
     * @return [Result] containing mock data, or DomainError if an error occurred
     **/
    suspend fun postFiles(kFiles: List<KFile>): Result<Data>
}