package com.inzynierka.data.models

import com.inzynierka.domain.models.RevisitedRankingEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RevisitedEntryDTO(
    val dimension: Int,
    @SerialName("algorithm_name")
    val algorithmName: String,
    @SerialName("function_number")
    val functionNumber: Int,
    @SerialName("successful_trials_percentage")
    val successfulTrialsPercentage: Double,
    @SerialName("thresholds_achieved_percentage")
    val thresholdsAchievedPercentage: Double,
    @SerialName("budget_left_percentage")
    val budgetLeftPercentage: Double,
    val score: Double,
)

fun RevisitedEntryDTO.toDomain() = RevisitedRankingEntry(
    rank = null,
    dimension = this.dimension,
    algorithmName = this.algorithmName,
    functionNumber = this.functionNumber,
    successfulTrialsPercentage = this.successfulTrialsPercentage,
    thresholdsAchievedPercentage = this.thresholdsAchievedPercentage,
    budgetLeftPercentage = this.budgetLeftPercentage,
    score = this.score
)
