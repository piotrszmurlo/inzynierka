package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.domain.models.Benchmark

data class RankingsState(
    val cec2022: ScoreRankingState = ScoreRankingState(),
    val pairTest: PairTestState = PairTestState(),
    val friedman: FriedmanRankingState = FriedmanRankingState(),
    val mean: StatisticsRankingState = StatisticsRankingState(),
    val revisited: RevisitedRankingState = RevisitedRankingState(),
    val ecdf: EcdfState = EcdfState(),
    val benchmarkNames: List<String> = listOf(),
    val selectedBenchmarkName: String? = null
)

sealed class RankingsAction : MainAppAction() {
    data class FetchAvailableBenchmarksSuccess(val benchmarks: List<Benchmark>) : RankingsAction()
    data class FetchAvailableBenchmarksFailed(val error: DomainError) : RankingsAction()
    data class BenchmarkSelected(val benchmarkName: String) : RankingsAction()
}

fun rankingsReducer(state: RankingsState, action: RankingsAction) = when (action) {
    is RankingsAction.FetchAvailableBenchmarksFailed -> state
    is RankingsAction.FetchAvailableBenchmarksSuccess -> state.copy(
        benchmarkNames = action.benchmarks.map { it.name },
        selectedBenchmarkName = state.selectedBenchmarkName?.let { benchmark ->
            if (action.benchmarks.map { it.name }
                    .contains(state.selectedBenchmarkName)) benchmark else action.benchmarks.firstOrNull()?.name
        } ?: action.benchmarks.firstOrNull()?.name
    )

    is RankingsAction.BenchmarkSelected -> state.copy(selectedBenchmarkName = action.benchmarkName)
    is Cec2022RankingAction -> state.copy(cec2022 = cec2022Reducer(state.cec2022, action))
    is PairTestAction -> state.copy(pairTest = pairTestReducer(state.pairTest, action))
    is FriedmanRankingAction -> state.copy(friedman = friedmanReducer(state.friedman, action))
    is StatisticsRankingAction -> state.copy(mean = statisticsReducer(state.mean, action))
    is RevisitedRankingAction -> state.copy(revisited = revisitedReducer(state.revisited, action))
    is EcdfAction -> state.copy(ecdf = ecdfReducer(state.ecdf, action))
}