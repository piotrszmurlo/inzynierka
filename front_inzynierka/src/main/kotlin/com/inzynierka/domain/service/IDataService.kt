package com.inzynierka.domain.service

import com.inzynierka.domain.Result
import com.inzynierka.model.Data
import com.inzynierka.model.RemoteCEC2022Data
import io.kvision.types.KFile

interface IDataService {

    /**
     * gets mock data for chart
     * @return [Result] containing the data, or DomainError if an error occurred
     **/
    suspend fun getData(): Result<Data>
    suspend fun getAvailableAlgorithms(): Result<List<String>>
    suspend fun getAvailableDimensions(): Result<List<Int>>

    suspend fun getCEC2022Scores(): Result<RemoteCEC2022Data>


    /**
     * uploads [kFiles] to the server
     * @return [Result] containing mock data, or DomainError if an error occurred
     **/
    suspend fun postFiles(kFiles: List<KFile>): Result<Unit>
}