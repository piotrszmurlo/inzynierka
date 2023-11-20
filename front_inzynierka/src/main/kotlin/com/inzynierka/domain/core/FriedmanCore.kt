package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.domain.models.ScoreRankingEntry

data class FriedmanRankingState(
    val scores: Map<Dimension, Map<FunctionNumber, List<ScoreRankingEntry>>>? = null,
    val combinedScores: List<ScoreRankingEntry>? = null,
    val isFetching: Boolean = false
)

sealed class FriedmanRankingAction : RankingsAction() {
    object FetchRankingsStarted : FriedmanRankingAction()
    data class FetchRankingsSuccess(val scores: List<ScoreRankingEntry>) : FriedmanRankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : FriedmanRankingAction()
}

fun friedmanReducer(state: FriedmanRankingState, action: FriedmanRankingAction) = when (action) {
    is FriedmanRankingAction.FetchRankingsSuccess -> action.scores.createFriedmanRankings()

    is FriedmanRankingAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is FriedmanRankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
}

fun List<ScoreRankingEntry>.createFriedmanRankings(): FriedmanRankingState {
    val scores = this
        .groupBy { it.dimension }
        .mapValues { (_, scoresGroupedByDimension) ->
            scoresGroupedByDimension
                .groupBy { it.functionNumber!! }
                .mapValues { (_, scores) ->
                    scores.sortedWith(compareBy { it.score })
                }
        }

    val combinedScores = this.groupBy { it.algorithmName }
        .mapValues { (_, scores) ->
            scores.reduce { acc, next ->
                acc.copy(score = acc.score + next.score)
            }.let { it.copy(score = it.score / scores.size) }
        }.values
        .sortedWith(compareBy { it.score })
        .mapIndexed { index, score ->
            score.copy(rank = index + 1)
        }
    return FriedmanRankingState(scores = scores, combinedScores = combinedScores, isFetching = false)
}