package com.inzynierka.data

import com.inzynierka.model.CEC2022Data
import com.inzynierka.model.Data
import io.kvision.types.KFile

interface IDataRepository {
    suspend fun getData(): Data
    suspend fun getCEC2022Scores(): CEC2022Data
    suspend fun postFiles(kFiles: List<KFile>)
}