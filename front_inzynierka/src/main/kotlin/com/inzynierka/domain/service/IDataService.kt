package com.inzynierka.domain.service

import com.inzynierka.common.Result
import com.inzynierka.domain.core.UserData
import com.inzynierka.domain.models.*
import com.inzynierka.model.BenchmarkData
import com.inzynierka.model.EcdfData
import io.kvision.types.KFile

interface IDataService {

    suspend fun getAvailableBenchmarks(): Result<List<Benchmark>>
    suspend fun getAvailableBenchmarkData(benchmarkName: String): Result<BenchmarkData>
    suspend fun getAvailableDimensions(benchmarkName: String): Result<List<Int>>
    suspend fun getCec2022Scores(benchmarkName: String): Result<List<ScoreRankingEntry>>
    suspend fun getFriedmanScores(benchmarkName: String): Result<List<ScoreRankingEntry>>
    suspend fun getStatisticsRankingEntries(benchmarkName: String): Result<List<StatisticsRankingEntry>>

    suspend fun getRevisitedRankingEntries(benchmarkName: String): Result<List<RevisitedRankingEntry>>
    suspend fun getEcdfData(benchmarkName: String): Result<List<EcdfData>>

    /*
     * uploads [kFiles] to the server
     * @return [Result] Unit or DomainError if an error occurred
     **/
    suspend fun postFiles(kFiles: List<KFile>, benchmarkName: String, overwriteExisting: Boolean): Result<Unit>
    suspend fun deleteFilesForAlgorithm(algorithmName: String, benchmarkName: String): Result<Unit>
    suspend fun loginUser(email: String, password: String): Result<Unit>
    suspend fun promoteUserToAdmin(email: String): Result<Unit>
    suspend fun registerUser(email: String, password: String): Result<Unit>
    suspend fun verifyAccount(code: String): Result<Unit>
    suspend fun resendVerificationCode(): Result<Unit>
    suspend fun getUserData(): Result<UserData>
    suspend fun createBenchmark(name: String, description: String, functionCount: Int, trialCount: Int): Result<Unit>

    suspend fun deleteBenchmark(benchmarkName: String): Result<Unit>

    suspend fun getPairTest(
        algorithm1: String,
        algorithm2: String,
        dimension: Int,
        benchmarkName: String
    ): Result<List<PairTestEntry>>

    suspend fun changePassword(newPassword: String, oldPassword: String): Result<Unit>
    suspend fun changeEmail(email: String): Result<Unit>
}