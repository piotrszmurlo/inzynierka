package com.inzynierka.domain.core

data class RankingsState(
    val cec2022: ScoreRankingState = ScoreRankingState(),
    val pairTest: PairTestState = PairTestState(),
    val friedman: ScoreRankingState = ScoreRankingState(),
    val mean: StatisticsRankingState = StatisticsRankingState(),
    val median: StatisticsRankingState = StatisticsRankingState(),
    val revisited: RevisitedRankingState = RevisitedRankingState()
)

sealed class RankingsAction : MainAppAction()

fun rankingsReducer(state: RankingsState, action: RankingsAction) = when (action) {
    is Cec2022RankingAction -> state.copy(cec2022 = cec2022Reducer(state.cec2022, action))
    is PairTestAction -> state.copy(pairTest = pairTestReducer(state.pairTest, action))
    is FriedmanRankingAction -> state.copy(friedman = friedmanReducer(state.friedman, action))
    is MeanRankingAction -> state.copy(mean = meanReducer(state.mean, action))
    is MedianRankingAction -> state.copy(median = medianReducer(state.median, action))
    is RevisitedRankingAction -> state.copy(revisited = revisitedReducer(state.revisited, action))
}