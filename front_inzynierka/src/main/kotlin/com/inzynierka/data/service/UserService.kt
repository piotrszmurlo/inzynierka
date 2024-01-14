package com.inzynierka.data.service

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.data.parsedRemoteExceptionMessage
import com.inzynierka.data.repository.IUserRepository
import com.inzynierka.domain.core.UserData
import com.inzynierka.domain.service.IUserService

class UserService(private val userRepository: IUserRepository) : IUserService {

    override suspend fun loginUser(email: String, password: String): Result<Unit> {
        return try {
            Result.Success(userRepository.loginUser(email, password))
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun promoteUserToAdmin(email: String): Result<Unit> {
        return try {
            Result.Success(userRepository.promoteUserToAdmin(email))
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun registerUser(email: String, password: String): Result<Unit> {
        return try {
            Result.Success(userRepository.registerUser(email, password))
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun verifyAccount(code: String): Result<Unit> {
        return try {
            Result.Success(userRepository.verifyAccount(code))
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun resendVerificationCode(): Result<Unit> {
        return try {
            Result.Success(userRepository.resendVerificationCode())
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun getUserData(): Result<UserData> {
        return try {
            val data = userRepository.getUserData()
            Result.Success(
                UserData(disabled = data.disabled, isUserAdmin = data.isAdmin)
            )
        } catch (e: Throwable) {
            Result.Error(
                DomainError(e.parsedRemoteExceptionMessage)
            )
        }
    }

    override suspend fun changePassword(newPassword: String, oldPassword: String): Result<Unit> {
        return try {
            Result.Success(userRepository.changePassword(newPassword, oldPassword))
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override suspend fun changeEmail(email: String): Result<Unit> {
        return try {
            Result.Success(userRepository.changeEmail(email))
        } catch (e: Throwable) {
            Result.Error(DomainError(e.parsedRemoteExceptionMessage))
        }
    }

    override fun logout() {
        userRepository
    }

}