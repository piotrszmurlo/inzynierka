package com.inzynierka.data.repository

import com.inzynierka.data.models.PairTestEntryDTO
import com.inzynierka.data.models.ScoreEntryDTO
import com.inzynierka.data.models.StatisticsEntryDTO
import io.kvision.types.KFile

interface IDataRepository {
    suspend fun getCec2022Scores(): List<ScoreEntryDTO>
    suspend fun getFriedmanScores(): List<ScoreEntryDTO>
    suspend fun getStatisticsEntries(): List<StatisticsEntryDTO>
    suspend fun getAvailableAlgorithms(): List<String>
    suspend fun getAvailableDimensions(): List<Int>
    suspend fun getAvailableFunctionNumbers(): List<Int>
    suspend fun postFiles(kFiles: List<KFile>)
    suspend fun getPairTest(
        firstAlgorithm: String,
        secondAlgorithm: String,
        dimension: Int
    ): List<PairTestEntryDTO>
}