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
            state.scores?.forEach {
                scoreRankingTable(
                    headerNames = listOf(RANK, ALGORITHM, scoreHeaderTitle),
                    title = DIMENSION_EQUALS(it.key),
                    scores = it.value
                )
            }
            state.combinedScores?.let {
                scoreRankingTable(
                    headerNames = listOf(RANK, ALGORITHM, combinedScoreHeaderTitle),
                    title = COMBINED_RANKING_TABLE_HEADER,
                    scores = it
                )
            }
        }
    }
}
