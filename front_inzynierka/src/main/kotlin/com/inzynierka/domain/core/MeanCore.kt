package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.data.models.BasicScoresDTO
import com.inzynierka.domain.service.IDataService
import io.kvision.redux.Dispatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class StatisticsRankingState(
    val isFetching: Boolean = false
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
    data class FetchRankingsSuccess(val scores: BasicScoresDTO) : MeanRankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : MeanRankingAction()
}

fun meanReducer(state: StatisticsRankingState, action: MeanRankingAction) = when (action) {
    is MeanRankingAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is MeanRankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
    is MeanRankingAction.FetchRankingsSuccess -> {
//        action.scores.entries.associate { entry ->
//            entry.key to entry.value
//                .entries.associate { entry2 ->
//                    entry2.key to entry2.value
//                        .entries.associate {entry3 ->
//                            entry3.key to entry3.value.
//                        }
//                }
//        }
        state.copy(isFetching = false)
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