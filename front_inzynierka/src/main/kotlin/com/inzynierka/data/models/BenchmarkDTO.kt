package com.inzynierka.data.models

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

