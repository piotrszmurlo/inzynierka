package com.inzynierka.data

import com.inzynierka.data.models.BasicScoresDTO
import com.inzynierka.model.RankingScores
import io.kvision.types.KFile

interface IDataRepository {
    suspend fun getCec2022Scores(): RankingScores
    suspend fun getFriedmanScores(): RankingScores
    suspend fun getBasicScores(): BasicScoresDTO
    suspend fun getAvailableAlgorithms(): List<String>
    suspend fun getAvailableDimensions(): List<Int>
    suspend fun getAvailableFunctionNumbers(): List<Int>
    suspend fun postFiles(kFiles: List<KFile>)
    suspend fun getPairTest(
        firstAlgorithm: String,
        secondAlgorithm: String,
        dimension: Int,
        functionNumber: Int
    ): String
}