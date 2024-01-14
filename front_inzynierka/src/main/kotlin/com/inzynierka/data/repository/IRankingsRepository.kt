package com.inzynierka.data.repository

import com.inzynierka.data.models.*

interface IRankingsRepository {
    suspend fun getCec2022Scores(benchmarkName: String): List<ScoreEntryDTO>
    suspend fun getFriedmanScores(benchmarkName: String): List<ScoreEntryDTO>
    suspend fun getStatisticsEntries(benchmarkName: String): List<StatisticsEntryDTO>
    suspend fun getRevisitedEntries(benchmarkName: String): List<RevisitedEntryDTO>
    suspend fun getEcdfData(benchmarkName: String): List<EcdfDataDTO>


    suspend fun getPairTest(
        firstAlgorithm: String,
        secondAlgorithm: String,
        dimension: Int,
        benchmarkName: String
    ): List<PairTestEntryDTO>
}