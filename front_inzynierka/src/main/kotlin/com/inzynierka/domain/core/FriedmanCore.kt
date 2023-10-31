package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.domain.models.ScoreRankingEntry
import com.inzynierka.domain.service.IDataService
import io.kvision.redux.Dispatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

fun loadFriedmanScores(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(FriedmanRankingAction.FetchRankingsStarted)
        when (val result = dataService.getFriedmanScores()) {
            is Result.Success -> dispatch(FriedmanRankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> dispatch(FriedmanRankingAction.FetchRankingsFailed(result.domainError))
        }
    }
}