package com.inzynierka.domain.service

import com.inzynierka.common.Result
import com.inzynierka.domain.core.UserData

interface IUserService {
    suspend fun loginUser(email: String, password: String): Result<Unit>
    suspend fun promoteUserToAdmin(email: String): Result<Unit>
    suspend fun registerUser(email: String, password: String): Result<Unit>
    suspend fun verifyAccount(code: String): Result<Unit>
    suspend fun resendVerificationCode(): Result<Unit>
    suspend fun getUserData(): Result<UserData>
    suspend fun changePassword(newPassword: String, oldPassword: String): Result<Unit>
    suspend fun changeEmail(email: String): Result<Unit>
    fun logout()
}