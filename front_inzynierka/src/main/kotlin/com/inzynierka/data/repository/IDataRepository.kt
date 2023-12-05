package com.inzynierka.data.repository

import com.inzynierka.data.models.*
import io.kvision.types.KFile

interface IDataRepository {
    suspend fun getCec2022Scores(): List<ScoreEntryDTO>
    suspend fun getFriedmanScores(): List<ScoreEntryDTO>
    suspend fun getStatisticsEntries(): List<StatisticsEntryDTO>
    suspend fun getRevisitedEntries(): List<RevisitedEntryDTO>
    suspend fun getEcdfData(): List<EcdfDataDTO>
    suspend fun getAvailableAlgorithms(): List<String>
    suspend fun getAvailableDimensions(): List<Int>
    suspend fun getAvailableFunctionNumbers(): List<Int>
    suspend fun postFiles(kFiles: List<KFile>, overwriteExisting: Boolean)
    suspend fun deleteFilesForAlgorithm(algorithmName: String)
    suspend fun loginUser(email: String, password: String)
    suspend fun registerUser(email: String, password: String)
    suspend fun getUserData(): UserData
    suspend fun promoteUserToAdmin(email: String)
    suspend fun verifyAccount(code: String)
    suspend fun resendVerificationCode()

    suspend fun getPairTest(
        firstAlgorithm: String,
        secondAlgorithm: String,
        dimension: Int
    ): List<PairTestEntryDTO>
}