package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.domain.models.StatisticsRankingEntry
import com.inzynierka.domain.service.IDataService
import io.kvision.redux.Dispatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


sealed class MedianRankingAction : RankingsAction() {
    object FetchRankingsStarted : MedianRankingAction()
    data class FetchRankingsSuccess(val scores: List<StatisticsRankingEntry>) : MedianRankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : MedianRankingAction()
    object ToggleNumberNotation : MedianRankingAction()
    data class ChangePrecision(val precision: Int) : MedianRankingAction()
}

fun medianReducer(state: StatisticsRankingState, action: MedianRankingAction) = when (action) {
    is MedianRankingAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is MedianRankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
    is MedianRankingAction.FetchRankingsSuccess -> {
        val sorted = action.scores.createRankings(
            compareBy({ score -> score.median },
                { score -> score.minEvaluations })
        )
        state.copy(isFetching = false, scores = sorted)
    }

    is MedianRankingAction.ChangePrecision -> state.copy(numberPrecision = action.precision)
    is MedianRankingAction.ToggleNumberNotation -> {
        state.copy(numberNotation = toggleNotation(state.numberNotation))
    }

}

fun List<StatisticsRankingEntry>.createRankings(comparator: Comparator<StatisticsRankingEntry>): Map<Int, Map<Int, List<StatisticsRankingEntry>>> {
    return this.groupBy { it.dimension }
        .mapValues {
            it.value
                .groupBy { score -> score.functionNumber }
                .mapValues { scores ->
                    scores.value
                        .sortedWith(comparator)
                        .mapIndexed { index, score -> score.copy(rank = index + 1) }
                }
        }
}

fun toggleNotation(numberNotation: NumberNotation): NumberNotation {
    return when (numberNotation) {
        is NumberNotation.Decimal -> NumberNotation.Scientific
        is NumberNotation.Scientific -> NumberNotation.Decimal
    }
}

fun loadMedianRanking(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(MedianRankingAction.FetchRankingsStarted)
        when (val result = dataService.getStatisticsRankingEntries()) {
            is Result.Success -> dispatch(MedianRankingAction.FetchRankingsSuccess(result.data))
            is Result.Error -> dispatch(MedianRankingAction.FetchRankingsFailed(result.domainError))
        }
    }
}