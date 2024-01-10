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
    is RevisitedRankingAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is RevisitedRankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
    is RevisitedRankingAction.FetchRankingsSuccess ->
        if (action.scores.isEmpty()) {
            state.copy(
                isFetching = false,
                scores = null,
                averagedScores = null
            )
        } else {
            val splitEntries = action.scores.groupBy { it.dimension }
                .mapValues { values ->
                    values.value
                        .groupBy { entry -> entry.functionNumber }
                        .mapValues { functionEntries ->
                            functionEntries.value
                                .sortedByDescending { entry -> entry.score }
                                .let { entries ->
                                    rankSortedList(
                                        entries,
                                        { it.score }) { el, rank -> el.copy(rank = rank) }
                                }
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
                .let { entries -> rankSortedList(entries, { it.score }) { el, rank -> el.copy(rank = rank) } }

            state.copy(
                isFetching = false,
                scores = splitEntries,
                averagedScores = averagedEntries
            )
        }
}

