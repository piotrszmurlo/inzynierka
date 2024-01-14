package com.inzynierka.data.repository

import com.inzynierka.data.models.UserData
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BearerToken(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String
)

var bearerToken: BearerToken? = null

class UserRepository(private val client: HttpClient) : IUserRepository {
    override suspend fun loginUser(email: String, password: String) {
        val token = client.submitForm(
            url = "users/login",
            formParameters = Parameters.build {
                append("username", email)
                append("password", password)
            }
        ).body<BearerToken>()
        bearerToken = token
    }

    override suspend fun registerUser(email: String, password: String) {
        val token = client.submitForm(
            url = "users/register",
            formParameters = Parameters.build {
                append("username", email)
                append("password", password)
            }
        ).body<BearerToken>()
        bearerToken = token
    }

    override suspend fun changePassword(newPassword: String, oldPassword: String) {
        client.submitForm(
            url = "users/password",
            formParameters = parameters {
                append("new_password", newPassword)
                append("old_password", oldPassword)
            }
        ) {
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }
    }

    override suspend fun changeEmail(email: String) {
        val token = client.submitForm(
            url = "users/email",
            formParameters = parameters {
                append("new_email", email)
            }
        ) {
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }.body<BearerToken>()
        bearerToken = token
    }

    override suspend fun promoteUserToAdmin(email: String) {
        return client.post(urlString = "/users/promote") {
            parameter("email", email)
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }.body()
    }

    override suspend fun verifyAccount(code: String) {
        client.post("/users/verify") {
            parameter("code", code)
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }
    }

    override suspend fun resendVerificationCode() {
        client.get("users/resend") {
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }
    }

    override suspend fun getUserData(): UserData {
        return client.get(urlString = "/users/me") {
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }.body()
    }
}