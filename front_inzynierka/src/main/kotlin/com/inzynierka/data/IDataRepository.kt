package com.inzynierka.data

import com.inzynierka.model.Data
import io.kvision.types.KFile

interface IDataRepository {
    suspend fun getData(): Data
    suspend fun postFile(kFile: KFile): Data
    suspend fun postFiles(kFiles: List<KFile>): Data
}