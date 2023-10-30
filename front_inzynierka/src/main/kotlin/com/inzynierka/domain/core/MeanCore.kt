package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.domain.service.IDataService
import io.kvision.redux.Dispatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class StatisticsRankingState(
    val isFetching: Boolean = false,
    val scores: Map<Int, Map<Int, List<StatisticsRankingEntry>>>? = null
)

data class StatisticsRankingEntry(
    val rank: Int?,
    val dimension: Int,
    val algorithmName: String,
    val functionNumber: Int,
    val mean: Double,
    val median: Double,
    val stdev: Double,
    val max: Double,
    val min: Double,
    val minEvaluations: Int
)

sealed class MeanRankingAction : RankingsAction() {
    object FetchRankingsStarted : MeanRankingAction()
    data class FetchRankingsSuccess(val scores: List<StatisticsRankingEntry>) : MeanRankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : MeanRankingAction()
}

fun meanReducer(state: StatisticsRankingState, action: MeanRankingAction) = when (action) {
    is MeanRankingAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is MeanRankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
    is MeanRankingAction.FetchRankingsSuccess -> {
        val sorted = action.scores.createRankings(compareBy({ score -> score.mean }, { score -> score.minEvaluations }))
        state.copy(isFetching = false, scores = sorted)
    }
}

fun loadMeanRanking(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(MeanRankingAction.FetchRankingsStarted)
        when (val result = dataService.getStatisticsRankingEntries()) {
            is Result.Success -> dispatch(MeanRankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> dispatch(MeanRankingAction.FetchRankingsFailed(result.domainError))
        }
    }
}