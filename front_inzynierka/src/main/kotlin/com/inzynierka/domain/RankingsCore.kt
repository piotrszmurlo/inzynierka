package com.inzynierka.domain

typealias Scores = Map<Int, List<Score>>

data class RankingsState(
    val cec2022: Cec2022RankingState = Cec2022RankingState(),
    val pairTest: PairTestState = PairTestState()
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
}