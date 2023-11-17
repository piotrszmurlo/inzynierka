package com.inzynierka.common

sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Error(val domainError: DomainError) : Result<Nothing>()
}


