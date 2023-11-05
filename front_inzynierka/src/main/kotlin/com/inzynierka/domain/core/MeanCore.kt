package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.domain.models.StatisticsRankingEntry
import com.inzynierka.domain.service.IDataService
import io.kvision.redux.Dispatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

typealias Dimension = Int
typealias FunctionNumber = Int

data class StatisticsRankingState(
    val isFetching: Boolean = false,
    val scores: Map<Dimension, Map<FunctionNumber, List<StatisticsRankingEntry>>>? = null,
    val numberNotation: NumberNotation = NumberNotation.Scientific,
    val numberPrecision: Int = 3
)

sealed class NumberNotation {
    object Scientific : NumberNotation()
    object Decimal : NumberNotation()
}

sealed class MeanRankingAction : RankingsAction() {
    object FetchRankingsStarted : MeanRankingAction()
    object ToggleNumberNotation : MeanRankingAction()
    data class ChangePrecision(val precision: Int) : MeanRankingAction()
    data class FetchRankingsSuccess(val scores: List<StatisticsRankingEntry>) : MeanRankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : MeanRankingAction()
}

fun meanReducer(state: StatisticsRankingState, action: MeanRankingAction) = when (action) {
    is MeanRankingAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is MeanRankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
    is MeanRankingAction.FetchRankingsSuccess -> {
        val sorted = action.scores.createRankings(compareBy({ it.mean }, { it.minEvaluations }))
        state.copy(isFetching = false, scores = sorted)
    }

    is MeanRankingAction.ToggleNumberNotation -> {
        state.copy(numberNotation = toggleNotation(state.numberNotation))
    }

    is MeanRankingAction.ChangePrecision -> state.copy(numberPrecision = action.precision)
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