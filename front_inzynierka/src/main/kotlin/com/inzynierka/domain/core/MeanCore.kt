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
    val scores: Map<Int, Map<Int, List<BasicScore>>>? = null
)

data class BasicScore(
    val rank: Int?,
    val dimension: Int,
    val algorithmName: String,
    val functionNumber: Int,
    val mean: Double,
    val median: Double,
    val stddev: Double,
    val max: Double,
    val min: Double,
    val minEvaluations: Int
)

data class StatisticTableEntry(
    val rank: Int,
    val algorithmName: String,
    val mean: Double,
    val median: Double,
    val stddev: Double,
    val min: Double,
    val max: Double,
)

sealed class MeanRankingAction : RankingsAction() {
    object FetchRankingsStarted : MeanRankingAction()
    data class FetchRankingsSuccess(val scores: List<BasicScore>) : MeanRankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : MeanRankingAction()
}

fun meanReducer(state: StatisticsRankingState, action: MeanRankingAction) = when (action) {
    is MeanRankingAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is MeanRankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
    is MeanRankingAction.FetchRankingsSuccess -> {
        val sorted = action.scores
            .groupBy { it.dimension }
            .mapValues {
                it.value
                    .groupBy { score -> score.functionNumber }
                    .mapValues { scores ->
                        scores.value
                            .sortedWith(compareBy({ score -> score.mean }, { score -> score.minEvaluations }))
                            .mapIndexed { index, score -> score.copy(rank = index + 1) }
                    }
            }
        state.copy(isFetching = false, scores = sorted)
    }
}

fun loadMeanRanking(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(MeanRankingAction.FetchRankingsStarted)
        when (val result = dataService.getBasicScores()) {
            is Result.Success -> dispatch(MeanRankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> dispatch(MeanRankingAction.FetchRankingsFailed(result.domainError))
        }
    }
}