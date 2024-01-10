package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.domain.models.StatisticsRankingEntry


sealed class MedianRankingAction : RankingsAction() {
    object FetchRankingsStarted : MedianRankingAction()
    object ErrorHandled : MedianRankingAction()
    data class FetchRankingsSuccess(val scores: List<StatisticsRankingEntry>) : MedianRankingAction()
    data class FetchRankingsFailed(val error: DomainError?) : MedianRankingAction()
    object ToggleNumberNotation : MedianRankingAction()
    data class ChangePrecision(val precision: Int) : MedianRankingAction()
}

fun medianReducer(state: StatisticsRankingState, action: MedianRankingAction) = when (action) {
    is MedianRankingAction.FetchRankingsFailed -> state.copy(isFetching = false, error = action.error)
    is MedianRankingAction.FetchRankingsStarted -> state.copy(isFetching = true)
    is MedianRankingAction.FetchRankingsSuccess -> {
        val sorted = action.scores.createRankings(
            compareBy({ score -> score.median },
                { score -> score.minEvaluations })
        ) { it.median }
        state.copy(isFetching = false, scores = sorted)
    }

    is MedianRankingAction.ChangePrecision -> state.copy(numberPrecision = action.precision)
    is MedianRankingAction.ToggleNumberNotation -> {
        state.copy(numberNotation = toggleNotation(state.numberNotation))
    }

    is MedianRankingAction.ErrorHandled -> state.copy(error = null)
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
                            val rankedEntries = mutableListOf<StatisticsRankingEntry>()
                            var rank = 1
                            entries.forEachIndexed { i, el ->
                                rankedEntries.add(el.copy(rank = rank))
                                if (i < entries.size - 1 && extractor(el) != extractor(entries[i + 1])) {
                                    rank++
                                }
                            }
                            rankedEntries
                        }
                }
        }
}

fun toggleNotation(numberNotation: NumberNotation): NumberNotation {
    return when (numberNotation) {
        is NumberNotation.Decimal -> NumberNotation.Scientific
        is NumberNotation.Scientific -> NumberNotation.Decimal
    }
}

