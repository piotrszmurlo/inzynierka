package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.ScoreRankingState
import com.inzynierka.ui.StringResources.ALGORITHM
import com.inzynierka.ui.StringResources.COMBINED_RANKING_TABLE_HEADER
import com.inzynierka.ui.StringResources.DIMENSION_EQUALS
import com.inzynierka.ui.StringResources.RANK
import com.inzynierka.ui.withLoadingSpinner
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.panel.flexPanel

fun Container.scoreRanking(state: ScoreRankingState, scoreHeaderTitle: String, combinedScoreHeaderTitle: String) {
    withLoadingSpinner(state.isFetching) {
        flexPanel(direction = FlexDirection.ROW) {
            val sortedDimensions = state.scores?.keys?.sorted()
            sortedDimensions?.forEach { dimension ->
                state.scores[dimension]?.let { scores ->
                    scoreRankingTable(
                        headerNames = listOf(RANK, ALGORITHM, scoreHeaderTitle),
                        title = DIMENSION_EQUALS(dimension),
                        scores = scores
                    )
                }
            }
            state.combinedScores?.let { combinedScores ->
                scoreRankingTable(
                    headerNames = listOf(RANK, ALGORITHM, combinedScoreHeaderTitle),
                    title = COMBINED_RANKING_TABLE_HEADER,
                    scores = combinedScores
                )
            }
        }
    }
}
