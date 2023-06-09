package com.inzynierka.data

sealed class DomainError {
    data class NetworkError(val message: String?) : DomainError()
    data class FileUploadError(val message: String?) : DomainError()
}