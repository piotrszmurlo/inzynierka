package com.inzynierka.domain.service

import com.inzynierka.common.Result
import com.inzynierka.model.BenchmarkData
import com.inzynierka.model.Cec2022Scores
import io.kvision.types.KFile

interface IDataService {

    suspend fun getAvailableBenchmarkData(): Result<BenchmarkData>
    suspend fun getAvailableDimensions(): Result<List<Int>>

    suspend fun getCec2022Scores(): Result<Cec2022Scores>


    /**
     * uploads [kFiles] to the server
     * @return [Result] containing mock data, or DomainError if an error occurred
     **/
    suspend fun postFiles(kFiles: List<KFile>): Result<Unit>

    suspend fun getPairTest(algorithm1: String, algorithm2: String, dimension: Int, functionNumber: Int): Result<String>
}