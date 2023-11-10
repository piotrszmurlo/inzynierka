package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.domain.models.ScoreRankingEntry


sealed class Cec2022RankingAction : RankingsAction() {
    object FetchRankingsStarted : Cec2022RankingAction()
    data class FetchRankingsSuccess(val scores: List<ScoreRankingEntry>) : Cec2022RankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : Cec2022RankingAction()
}

fun cec2022Reducer(state: ScoreRankingState, action: Cec2022RankingAction) = when (action) {
    is Cec2022RankingAction.FetchRankingsSuccess -> action.scores.createRankings(compareByDescending { score -> score.score })
    is Cec2022RankingAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is Cec2022RankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
}


