package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.domain.models.ScoreRankingEntry
import com.inzynierka.domain.service.IDataService
import com.inzynierka.ui.show
import io.kvision.redux.Dispatch
import io.kvision.toast.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


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

fun loadCec2022Scores(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(Cec2022RankingAction.FetchRankingsStarted)
        when (val result = dataService.getCec2022Scores()) {
            is Result.Success -> dispatch(Cec2022RankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> {
                Toast.show("Ranking fetch failed")
                dispatch(Cec2022RankingAction.FetchRankingsFailed(result.domainError))
            }
        }
    }
}
