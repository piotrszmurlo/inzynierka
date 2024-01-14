package com.inzynierka.data.service

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.data.parsedRemoteExceptionMessage
import com.inzynierka.data.repository.IFileRepository
import com.inzynierka.domain.service.IFileService
import io.kvision.types.KFile

class FileService(private val fileRepository: IFileRepository) : IFileService {
    override suspend fun postFiles(
        kFiles: List<KFile>,
        benchmarkName: String,
        overwriteExisting: Boolean
    ): Result<Unit> {
        return try {
            Result.Success(fileRepository.postFiles(kFiles, benchmarkName, overwriteExisting))
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun deleteFilesForAlgorithm(algorithmName: String, benchmarkName: String): Result<Unit> {
        return try {
            Result.Success(fileRepository.deleteFilesForAlgorithm(algorithmName, benchmarkName))
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }
}