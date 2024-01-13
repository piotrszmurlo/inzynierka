package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.domain.models.StatisticsRankingEntry

typealias Dimension = Int
typealias FunctionNumber = Int

val AVAILABLE_PRECISIONS = listOf(1, 2, 3, 4, 5, 6, 7, 8)

data class StatisticsRankingState(
    val isFetching: Boolean = false,
    val scores: Map<Dimension, Map<FunctionNumber, List<StatisticsRankingEntry>>>? = null,
    val numberNotation: NumberNotation = NumberNotation.Scientific,
    val numberPrecision: Int = 3,
    val error: DomainError? = null
) {
    val availablePrecisions
        get() = AVAILABLE_PRECISIONS
}

sealed class StatisticsRankingType {
    object Mean : StatisticsRankingType()
    object Median : StatisticsRankingType()

}

sealed class NumberNotation {
    object Scientific : NumberNotation()
    object Decimal : NumberNotation()
}

sealed class StatisticsRankingAction : RankingsAction() {
    object ErrorHandled : StatisticsRankingAction()
    object FetchRankingsStarted : StatisticsRankingAction()
    object ToggleNumberNotation : StatisticsRankingAction()
    data class ChangePrecision(val precision: Int) : StatisticsRankingAction()
    data class FetchRankingsSuccess(
        val scores: List<StatisticsRankingEntry>,
        val statisticsRankingType: StatisticsRankingType
    ) :
        StatisticsRankingAction()

    data class FetchRankingsFailed(val error: DomainError?) : StatisticsRankingAction()
}

fun statisticsReducer(state: StatisticsRankingState, action: StatisticsRankingAction) = when (action) {
    is StatisticsRankingAction.FetchRankingsFailed -> state.copy(isFetching = false, error = action.error)
    is StatisticsRankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
    is StatisticsRankingAction.FetchRankingsSuccess -> {
        val sorted = action.scores.createRankings(compareBy({ it.mean }, { it.minEvaluations })) {
            if (action.statisticsRankingType is StatisticsRankingType.Mean) it.mean else it.mean
        }
        state.copy(isFetching = false, scores = sorted)
    }

    is StatisticsRankingAction.ToggleNumberNotation -> {
        state.copy(numberNotation = toggleNotation(state.numberNotation))
    }

    is StatisticsRankingAction.ChangePrecision -> state.copy(numberPrecision = action.precision)
    is StatisticsRankingAction.ErrorHandled -> state.copy(error = null)
}

fun List<StatisticsRankingEntry>.createRankings(
    comparator: Comparator<StatisticsRankingEntry>,
    extractor: (StatisticsRankingEntry) -> Double
): Map<Int, Map<Int, List<StatisticsRankingEntry>>> {
    return this.groupBy { it.dimension }
        .mapValues {
            it.value
                .groupBy { score -> score.functionNumber }
                .mapValues { scores ->
                    scores.value
                        .sortedWith(comparator)
                        .let { entries ->
                            rankSortedList(
                                entries,
                                extractor
                            ) { entry, rank -> entry.copy(rank = rank) }
                        }
                }
        }
}

inline fun <reified T : Any> rankSortedList(
    list: List<T>,
    extractor: (T) -> Double,
    rankTFactory: (T, Int) -> T
): List<T> {
    val rankedEntries = mutableListOf<T>()
    var rank = 1
    list.forEachIndexed { i, el ->
        rankedEntries.add(rankTFactory(el, rank))
        if (i < list.size - 1 && extractor(el) != extractor(list[i + 1])) {
            rank++
        }
    }
    return rankedEntries
}

fun toggleNotation(numberNotation: NumberNotation): NumberNotation {
    return when (numberNotation) {
        is NumberNotation.Decimal -> NumberNotation.Scientific
        is NumberNotation.Scientific -> NumberNotation.Decimal
    }
}