package com.inzynierka.data

import com.inzynierka.domain.DomainError
import com.inzynierka.domain.Result
import com.inzynierka.domain.service.IDataService
import com.inzynierka.model.RemoteCEC2022Data
import io.kvision.types.KFile

class DataService(private val dataRepository: IDataRepository) : IDataService {

    override suspend fun getAvailableAlgorithms(): Result<List<String>> {
        return try {
            val result = dataRepository.getAvailableAlgorithms()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(DomainError.NetworkError(e.message))
        }
    }

    override suspend fun getAvailableDimensions(): Result<List<Int>> {
        return try {
            val result = dataRepository.getAvailableDimensions()
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(DomainError.NetworkError(e.message))
        }
    }

    override suspend fun getCEC2022Scores(): Result<RemoteCEC2022Data> {
        return try {
            val result = dataRepository.getCEC2022Scores()
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

    override suspend fun getPairTest(
        algorithm1: String,
        algorithm2: String,
        dimension: Int,
        functionNumber: Int
    ): Result<String> {
        return try {
            val result = dataRepository.getPairTest(algorithm1, algorithm2, dimension, functionNumber)
            Result.Success(result)
        } catch (e: Exception) {
            Result.Error(DomainError.FileUploadError(e.message))
        }
    }
}