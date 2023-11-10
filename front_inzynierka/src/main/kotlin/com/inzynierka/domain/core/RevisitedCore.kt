package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.domain.models.RevisitedRankingEntry


data class RevisitedRankingState(
    val scores: Map<Dimension, Map<FunctionNumber, List<RevisitedRankingEntry>>>? = null,
    val averagedScores: List<RevisitedRankingEntry>? = null,
    val isFetching: Boolean = false
)

sealed class RevisitedRankingAction : RankingsAction() {
    object FetchRankingsStarted : RevisitedRankingAction()
    data class FetchRankingsSuccess(val scores: List<RevisitedRankingEntry>) : RevisitedRankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : RevisitedRankingAction()
}

fun revisitedReducer(state: RevisitedRankingState, action: RevisitedRankingAction) = when (action) {
    is RevisitedRankingAction.FetchRankingsFailed -> state.copy(isFetching = true)
    is RevisitedRankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
    is RevisitedRankingAction.FetchRankingsSuccess -> {
        val splitEntries = action.scores.groupBy { it.dimension }
            .mapValues {
                it.value
                    .groupBy { entry -> entry.functionNumber }
                    .mapValues { entries ->
                        entries.value
                            .sortedByDescending { entry -> entry.score }
                            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }
                    }
            }
        var entriesPerAlgorithm: Int
        val averagedEntries = action.scores.groupBy { entry -> entry.algorithmName }.values
            .also { entriesPerAlgorithm = it.elementAt(0).count() }
            .map {
                it.reduce { acc, next ->
                    acc.copy(
                        successfulTrialsPercentage = acc.successfulTrialsPercentage + next.successfulTrialsPercentage,
                        thresholdsAchievedPercentage = acc.thresholdsAchievedPercentage + next.thresholdsAchievedPercentage,
                        budgetLeftPercentage = acc.budgetLeftPercentage + next.budgetLeftPercentage,
                        score = acc.score + next.score
                    )
                }
            }
            .map {
                it.copy(
                    successfulTrialsPercentage = it.successfulTrialsPercentage / entriesPerAlgorithm,
                    thresholdsAchievedPercentage = it.thresholdsAchievedPercentage / entriesPerAlgorithm,
                    budgetLeftPercentage = it.budgetLeftPercentage / entriesPerAlgorithm,
                    score = it.score / entriesPerAlgorithm
                )
            }
            .sortedByDescending { it.score }
            .mapIndexed { index, entry -> entry.copy(rank = index + 1) }

        state.copy(
            isFetching = false,
            scores = splitEntries,
            averagedScores = averagedEntries
        )
    }
}

