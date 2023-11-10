package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.domain.models.ScoreRankingEntry


sealed class FriedmanRankingAction : RankingsAction() {
    object FetchRankingsStarted : FriedmanRankingAction()
    data class FetchRankingsSuccess(val scores: List<ScoreRankingEntry>) : FriedmanRankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : FriedmanRankingAction()
}

fun friedmanReducer(state: ScoreRankingState, action: FriedmanRankingAction) = when (action) {
    is FriedmanRankingAction.FetchRankingsSuccess -> {
        action.scores.createRankings(compareBy { it.score })
    }

    is FriedmanRankingAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is FriedmanRankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
}

