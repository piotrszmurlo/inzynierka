package com.inzynierka.data.models

import com.inzynierka.domain.models.Benchmark
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BenchmarkDTO(
    val id: Int,
    val name: String,
    val description: String,
    @SerialName("function_count")
    val functionCount: Int,
    @SerialName("trial_count")
    val trialCount: Int
)

fun BenchmarkDTO.toDomain() = Benchmark(
    name = name,
    description = description,
    functionCount = functionCount,
    trialCount = trialCount

)