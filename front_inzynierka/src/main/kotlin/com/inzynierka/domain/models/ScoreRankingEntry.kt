package com.inzynierka.domain.models

import com.inzynierka.domain.core.FunctionNumber

data class ScoreRankingEntry(
    val rank: Int?,
    val dimension: Int,
    val algorithmName: String,
    val functionNumber: FunctionNumber?,
    val score: Double,
)