package com.inzynierka.domain.models

data class RevisitedRankingEntry(
    val rank: Int?,
    val dimension: Int,
    val algorithmName: String,
    val functionNumber: Int,
    val successfulTrialsPercentage: Double,
    val thresholdsAchievedPercentage: Double,
    val budgetLeftPercentage: Double,
    val score: Double,
)