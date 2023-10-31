package com.inzynierka.domain.models

data class StatisticsRankingEntry(
    val rank: Int?,
    val dimension: Int,
    val algorithmName: String,
    val functionNumber: Int,
    val mean: Double,
    val median: Double,
    val stdev: Double,
    val max: Double,
    val min: Double,
    val minEvaluations: Int
)