package com.inzynierka.data

import com.inzynierka.domain.DomainError
import com.inzynierka.domain.Result
import com.inzynierka.domain.service.IDataService
import com.inzynierka.model.Data
import io.kvision.types.KFile

class DataService(private val dataRepository: IDataRepository) : IDataService {

    override suspend fun getData(): Result<Data> {
        return try {
            val result = dataRepository.getData()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(DomainError.NetworkError(e.message))
        }
    }

    override suspend fun postFiles(kFiles: List<KFile>): Result<Unit> {
        return try {
            val result = dataRepository.postFiles(kFiles)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(DomainError.FileUploadError(e.message))
        }
    }
}