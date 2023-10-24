package com.inzynierka.domain.service

import com.inzynierka.common.Result
import com.inzynierka.data.models.BasicScoresDTO
import com.inzynierka.model.BenchmarkData
import com.inzynierka.model.RankingScores
import io.kvision.types.KFile

interface IDataService {

    suspend fun getAvailableBenchmarkData(): Result<BenchmarkData>
    suspend fun getAvailableDimensions(): Result<List<Int>>

    suspend fun getCec2022Scores(): Result<RankingScores>
    suspend fun getFriedmanScores(): Result<RankingScores>
    suspend fun getBasicScores(): Result<BasicScoresDTO>


    /**
     * uploads [kFiles] to the server
     * @return [Result] containing mock data, or DomainError if an error occurred
     **/
    suspend fun postFiles(kFiles: List<KFile>): Result<Unit>

    suspend fun getPairTest(algorithm1: String, algorithm2: String, dimension: Int, functionNumber: Int): Result<String>
}