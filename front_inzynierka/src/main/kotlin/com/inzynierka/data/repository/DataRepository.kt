package com.inzynierka.data.repository

import com.inzynierka.data.actualFileContentOnly
import com.inzynierka.data.models.*
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.kvision.types.KFile
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BearerToken(
    @SerialName("access_token")
    val accessToken: String,
    @SerialName("token_type")
    val tokenType: String
)

@Serializable
data class UserData(
    val email: String,
    val disabled: Boolean,
    @SerialName("is_admin")
    val isAdmin: Boolean
)

private var bearerToken: BearerToken? = null

class DataRepository(private val client: HttpClient) : IDataRepository {

    override suspend fun getCec2022Scores(benchmarkName: String): List<ScoreEntryDTO> {
        return client.get(urlString = "rankings/cec2022") {
            parameter("benchmark_name", benchmarkName)
        }.body()
    }

    override suspend fun getFriedmanScores(benchmarkName: String): List<ScoreEntryDTO> {
        return client.get(urlString = "rankings/friedman") {
            parameter("benchmark_name", benchmarkName)
        }.body()
    }

    override suspend fun getStatisticsEntries(benchmarkName: String): List<StatisticsEntryDTO> {
        return client.get(urlString = "rankings/statistics") {
            parameter("benchmark_name", benchmarkName)
        }.body()
    }

    override suspend fun getRevisitedEntries(benchmarkName: String): List<RevisitedEntryDTO> {
        return client.get(urlString = "rankings/revisited") {
            parameter("benchmark_name", benchmarkName)
        }.body()
    }

    override suspend fun getEcdfData(benchmarkName: String): List<EcdfDataDTO> {
        return client.get(urlString = "rankings/ecdf") {
            parameter("benchmark_name", benchmarkName)
        }.body()
    }

    override suspend fun getAvailableAlgorithms(benchmarkName: String): List<String> {
        return client.get(urlString = "algorithms/$benchmarkName").body()
    }

    override suspend fun getAvailableDimensions(benchmarkName: String): List<Int> {
        return client.get(urlString = "dimensions/$benchmarkName").body()
    }

    override suspend fun getAvailableFunctionNumbers(benchmarkName: String): List<Int> {
        return client.get(urlString = "functions/$benchmarkName").body()
    }

    override suspend fun deleteFilesForAlgorithm(algorithmName: String, benchmarkName: String) {
        return client.delete(urlString = "file/$algorithmName") {
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
            parameter("benchmark_name", benchmarkName)
        }.body()
    }

    override suspend fun loginUser(email: String, password: String) {
        val token = client.submitForm(
            url = "token",
            formParameters = Parameters.build {
                append("username", email)
                append("password", password)
            }
        ).body<BearerToken>()
        bearerToken = token
    }

    override suspend fun registerUser(email: String, password: String) {
        val token = client.submitForm(
            url = "register",
            formParameters = Parameters.build {
                append("username", email)
                append("password", password)
            }
        ).body<BearerToken>()
        bearerToken = token
    }

    override suspend fun getUserData(): UserData {
        return client.get(urlString = "/users/me") {
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }.body()
    }

    override suspend fun getAvailableBenchmarks(): List<BenchmarkDTO> {
        return client.get(urlString = "benchmarks").body()
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

    override suspend fun postFiles(kFiles: List<KFile>, benchmarkName: String, overwriteExisting: Boolean) {
        client.submitFormWithBinaryData(
            url = "file",
            formData = formData {
                kFiles.forEach { file ->
                    this.append("files", file.actualFileContentOnly!!, Headers.build {
                        append(HttpHeaders.ContentType, "multipart/form-data")
                        append(HttpHeaders.ContentDisposition, "filename=\"${file.name}\"")
                    })
                }
            }
        ) {
            parameter("overwrite", overwriteExisting)
            parameter("benchmark", benchmarkName)
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }
    }

    override suspend fun postBenchmark(name: String, description: String, functionCount: Int, trialCount: Int) {
        client.submitForm(
            url = "benchmarks",
            formParameters = Parameters.build {
                this.append("name", name)
                this.append("description", description)
                this.append("function_count", functionCount.toString())
                this.append("trial_count", trialCount.toString())
            }
        ) {
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }
    }

    override suspend fun deleteBenchmark(benchmarkName: String) {
        client.delete(urlString = "benchmarks/$benchmarkName") {
            header("Authorization", "Bearer ${bearerToken?.accessToken}")
        }
    }

    override suspend fun getPairTest(
        firstAlgorithm: String,
        secondAlgorithm: String,
        dimension: Int,
        benchmarkName: String
    ): List<PairTestEntryDTO> {
        return client.submitForm(
            url = "rankings/wilcoxon",
            formParameters = parameters {
                append("first_algorithm", firstAlgorithm)
                append("second_algorithm", secondAlgorithm)
                append("dimension", "$dimension")
                append("benchmark_name", benchmarkName)
            }
        ).body()
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
}