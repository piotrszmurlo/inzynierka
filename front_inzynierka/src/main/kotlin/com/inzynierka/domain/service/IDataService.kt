package com.inzynierka.domain.service

import com.github.michaelbull.result.Result
import com.inzynierka.data.DomainError
import com.inzynierka.model.Data
import io.kvision.types.KFile

interface IDataService {

    suspend fun getData(): Result<Data, DomainError>

    suspend fun postFile(kFile: KFile): Result<Unit, DomainError>
}