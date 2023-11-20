package com.inzynierka.data.models

import com.inzynierka.domain.models.ScoreRankingEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScoreEntryDTO(
    val dimension: Int,
    @SerialName("algorithm_name")
    val algorithmName: String,
    @SerialName("function_number")
    val functionNumber: Int?,
    val score: Double,
)

fun ScoreEntryDTO.toDomain() = ScoreRankingEntry(
    rank = null,
    dimension = this.dimension,
    algorithmName = this.algorithmName,
    functionNumber = this.functionNumber,
    score = this.score
)
