package com.inzynierka.domain.service

import com.inzynierka.common.Result
import com.inzynierka.domain.models.PairTestEntry
import com.inzynierka.domain.models.RevisitedRankingEntry
import com.inzynierka.domain.models.ScoreRankingEntry
import com.inzynierka.domain.models.StatisticsRankingEntry
import com.inzynierka.model.BenchmarkData
import com.inzynierka.model.EcdfData
import io.kvision.types.KFile

interface IDataService {

    suspend fun getAvailableBenchmarkData(): Result<BenchmarkData>
    suspend fun getAvailableDimensions(): Result<List<Int>>
    suspend fun getCec2022Scores(): Result<List<ScoreRankingEntry>>
    suspend fun getFriedmanScores(): Result<List<ScoreRankingEntry>>
    suspend fun getStatisticsRankingEntries(): Result<List<StatisticsRankingEntry>>

    suspend fun getRevisitedRankingEntries(): Result<List<RevisitedRankingEntry>>
    suspend fun getEcdfData(): Result<List<EcdfData>>

    /*
     * uploads [kFiles] to the server
     * @return [Result] Unit or DomainError if an error occurred
     **/
    suspend fun postFiles(kFiles: List<KFile>): Result<Unit>
    suspend fun deleteFilesForAlgorithm(algorithmName: String): Result<Unit>
    suspend fun loginUser(email: String, password: String): Result<Unit>
    suspend fun registerUser(email: String, password: String): Result<Unit>
    suspend fun isCurrentUserAdmin(): Result<Boolean>
    suspend fun getPairTest(algorithm1: String, algorithm2: String, dimension: Int): Result<List<PairTestEntry>>
}