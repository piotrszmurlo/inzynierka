package com.inzynierka.domain.service

import com.inzynierka.common.Result
import io.kvision.types.KFile

interface IFileService {
    /*
 * uploads [kFiles] to the server
 * @return [Result] Unit or DomainError if an error occurred
 **/
    suspend fun postFiles(kFiles: List<KFile>, benchmarkName: String, overwriteExisting: Boolean): Result<Unit>
    suspend fun deleteFilesForAlgorithm(algorithmName: String, benchmarkName: String): Result<Unit>
}