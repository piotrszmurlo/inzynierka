package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.ScoreRankingState
import com.inzynierka.ui.withLoadingSpinner
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.panel.flexPanel

fun Container.scoreRanking(state: ScoreRankingState, scoreHeaderTitle: String, combinedScoreHeaderTitle: String) {
    withLoadingSpinner(state.isFetching) {
        flexPanel(direction = FlexDirection.ROW) {
            state.scores?.forEach {
                scoreRankingTable(
                    headerNames = listOf("Rank", "Algorithm", scoreHeaderTitle),
                    title = "Dimension = ${it.key}",
                    scores = it.value
                )
            }
            state.combinedScores?.let {
                scoreRankingTable(
                    headerNames = listOf("Rank", "Algorithm", combinedScoreHeaderTitle),
                    title = "Combined ranking",
                    scores = it
                )
            }
        }
    }
}
