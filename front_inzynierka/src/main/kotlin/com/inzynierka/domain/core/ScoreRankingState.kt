package com.inzynierka.domain.core

data class ScoreRankingState(
    val scores: Scores? = null,
    val combinedScores: List<Score>? = null,
    val isFetching: Boolean = false
)