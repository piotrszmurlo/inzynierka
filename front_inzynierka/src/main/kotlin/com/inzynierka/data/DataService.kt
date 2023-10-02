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
        } catch (a: IllegalArgumentException) {
            Result.Error(DomainError.NetworkError("123"))
        }
    }

    override suspend fun postFiles(kFiles: List<KFile>): Result<Data> {
        return try {
            val result = dataRepository.postFiles(kFiles)
            Result.Success(result)
        } catch (a: Exception) {
            Result.Error(DomainError.FileUploadError("err"))
        }
    }
}