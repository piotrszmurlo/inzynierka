package com.inzynierka.domain.models

data class ScoreRankingEntry(
    val rank: Int?,
    val dimension: Int,
    val algorithmName: String,
    val score: Double,
)