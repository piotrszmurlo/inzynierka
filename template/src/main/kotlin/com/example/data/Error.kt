package com.example.data

sealed class DomainError {
    data class NetworkError(val message: String) : DomainError()
}