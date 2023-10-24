package com.inzynierka.domain.core

typealias Scores = Map<Int, List<Score>>

data class RankingsState(
    val cec2022: ScoreRankingState = ScoreRankingState(),
    val pairTest: PairTestState = PairTestState(),
    val friedman: ScoreRankingState = ScoreRankingState(),
    val mean: StatisticsRankingState = StatisticsRankingState()
)

sealed class RankingsAction : MainAppAction()

data class Score(
    val rank: Int,
    val algorithmName: String,
    val score: Double
)

fun rankingsReducer(state: RankingsState, action: RankingsAction) = when (action) {
    is Cec2022RankingAction -> state.copy(cec2022 = cec2022Reducer(state.cec2022, action))
    is PairTestAction -> state.copy(pairTest = pairTestReducer(state.pairTest, action))
    is FriedmanRankingAction -> state.copy(friedman = friedmanReducer(state.friedman, action))
    is MeanRankingAction -> state.copy(mean = meanReducer(state.mean, action))
}