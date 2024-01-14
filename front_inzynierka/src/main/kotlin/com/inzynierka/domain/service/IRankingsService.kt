package com.inzynierka.domain.service

import com.inzynierka.common.Result
import com.inzynierka.domain.models.PairTestEntry
import com.inzynierka.domain.models.RevisitedRankingEntry
import com.inzynierka.domain.models.ScoreRankingEntry
import com.inzynierka.domain.models.StatisticsRankingEntry
import com.inzynierka.model.EcdfData

interface IRankingsService {
    suspend fun getCec2022Scores(benchmarkName: String): Result<List<ScoreRankingEntry>>
    suspend fun getFriedmanScores(benchmarkName: String): Result<List<ScoreRankingEntry>>
    suspend fun getStatisticsRankingEntries(benchmarkName: String): Result<List<StatisticsRankingEntry>>

    suspend fun getRevisitedRankingEntries(benchmarkName: String): Result<List<RevisitedRankingEntry>>
    suspend fun getEcdfData(benchmarkName: String): Result<List<EcdfData>>


    suspend fun getPairTest(
        algorithm1: String,
        algorithm2: String,
        dimension: Int,
        benchmarkName: String
    ): Result<List<PairTestEntry>>
}