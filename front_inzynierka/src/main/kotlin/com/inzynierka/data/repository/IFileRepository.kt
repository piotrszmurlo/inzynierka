package com.inzynierka.data.repository

import io.kvision.types.KFile

interface IFileRepository {
    suspend fun deleteFilesForAlgorithm(algorithmName: String, benchmarkName: String)
    suspend fun postFiles(kFiles: List<KFile>, benchmarkName: String, overwriteExisting: Boolean)
}