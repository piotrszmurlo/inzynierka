package com.inzynierka.data.repository

import com.inzynierka.data.models.*
import io.kvision.types.KFile

interface IDataRepository {
    suspend fun getCec2022Scores(benchmarkName: String): List<ScoreEntryDTO>
    suspend fun getFriedmanScores(benchmarkName: String): List<ScoreEntryDTO>
    suspend fun getStatisticsEntries(benchmarkName: String): List<StatisticsEntryDTO>
    suspend fun getRevisitedEntries(benchmarkName: String): List<RevisitedEntryDTO>
    suspend fun getEcdfData(benchmarkName: String): List<EcdfDataDTO>
    suspend fun getAvailableAlgorithms(benchmarkName: String): List<String>
    suspend fun getAvailableDimensions(benchmarkName: String): List<Int>
    suspend fun getAvailableFunctionNumbers(benchmarkName: String): List<Int>
    suspend fun postFiles(kFiles: List<KFile>, benchmarkName: String, overwriteExisting: Boolean)
    suspend fun postBenchmark(name: String, description: String, functionCount: Int, trialCount: Int)
    suspend fun deleteBenchmark(benchmarkName: String)
    suspend fun deleteFilesForAlgorithm(algorithmName: String, benchmarkName: String)
    suspend fun loginUser(email: String, password: String)
    suspend fun registerUser(email: String, password: String)
    suspend fun getUserData(): UserData
    suspend fun getAvailableBenchmarks(): List<BenchmarkDTO>

    suspend fun promoteUserToAdmin(email: String)
    suspend fun verifyAccount(code: String)
    suspend fun resendVerificationCode()

    suspend fun getPairTest(
        firstAlgorithm: String,
        secondAlgorithm: String,
        dimension: Int,
        benchmarkName: String
    ): List<PairTestEntryDTO>

    suspend fun changePassword(newPassword: String, oldPassword: String)
    suspend fun changeEmail(email: String)
}