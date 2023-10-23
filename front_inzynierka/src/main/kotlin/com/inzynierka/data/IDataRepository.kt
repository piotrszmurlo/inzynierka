package com.inzynierka.data

import com.inzynierka.model.RemoteCEC2022Data
import io.kvision.types.KFile

interface IDataRepository {
    suspend fun getCec2022Scores(): RemoteCEC2022Data

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