package com.inzynierka.data.repository

import com.inzynierka.data.models.UserData

interface IUserRepository {
    suspend fun loginUser(email: String, password: String)
    suspend fun registerUser(email: String, password: String)
    suspend fun getUserData(): UserData
    suspend fun promoteUserToAdmin(email: String)
    suspend fun changePassword(newPassword: String, oldPassword: String)
    suspend fun changeEmail(email: String)
    suspend fun verifyAccount(code: String)
    suspend fun resendVerificationCode()
}