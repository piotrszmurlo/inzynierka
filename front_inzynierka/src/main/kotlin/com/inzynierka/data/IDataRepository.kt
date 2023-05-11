package com.inzynierka.data

import com.github.michaelbull.result.Result
import com.inzynierka.model.Data
import io.kvision.types.KFile

interface IDataRepository {
    suspend fun getData(): Result<Data, DomainError>
    suspend fun postFile(kFile: KFile): Result<Unit, DomainError>
}