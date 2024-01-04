package com.inzynierka.domain.core

import com.inzynierka.common.DomainError

data class RankingsState(
    val cec2022: ScoreRankingState = ScoreRankingState(),
    val pairTest: PairTestState = PairTestState(),
    val friedman: FriedmanRankingState = FriedmanRankingState(),
    val mean: StatisticsRankingState = StatisticsRankingState(),
    val median: StatisticsRankingState = StatisticsRankingState(),
    val revisited: RevisitedRankingState = RevisitedRankingState(),
    val ecdf: EcdfState = EcdfState(),
    val benchmarkNames: List<String> = listOf(),
    val selectedBenchmarkName: String? = null
)

sealed class RankingsAction : MainAppAction() {
    data class FetchAvailableBenchmarksSuccess(val benchmarkNames: List<String>) : RankingsAction()
    data class FetchAvailableBenchmarksFailed(val error: DomainError) : RankingsAction()
    data class BenchmarkSelected(val benchmarkName: String) : RankingsAction()
}

fun rankingsReducer(state: RankingsState, action: RankingsAction) = when (action) {
    is RankingsAction.FetchAvailableBenchmarksFailed -> state
    is RankingsAction.FetchAvailableBenchmarksSuccess -> state.copy(
        benchmarkNames = action.benchmarkNames,
        selectedBenchmarkName = state.selectedBenchmarkName?.let { benchmark ->
            if (action.benchmarkNames.contains(state.selectedBenchmarkName)) benchmark else action.benchmarkNames.firstOrNull()
        } ?: action.benchmarkNames.firstOrNull()
    )

    is RankingsAction.BenchmarkSelected -> state.copy(selectedBenchmarkName = action.benchmarkName)
    is Cec2022RankingAction -> state.copy(cec2022 = cec2022Reducer(state.cec2022, action))
    is PairTestAction -> state.copy(pairTest = pairTestReducer(state.pairTest, action))
    is FriedmanRankingAction -> state.copy(friedman = friedmanReducer(state.friedman, action))
    is MeanRankingAction -> state.copy(mean = meanReducer(state.mean, action))
    is MedianRankingAction -> state.copy(median = medianReducer(state.median, action))
    is RevisitedRankingAction -> state.copy(revisited = revisitedReducer(state.revisited, action))
    is EcdfAction -> state.copy(ecdf = ecdfReducer(state.ecdf, action))
}