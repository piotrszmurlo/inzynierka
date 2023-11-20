package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.FriedmanRankingState
import com.inzynierka.ui.StringResources
import com.inzynierka.ui.divider
import com.inzynierka.ui.withLoadingSpinner
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.html.h5
import io.kvision.panel.flexPanel

fun Container.friedmanTab(state: FriedmanRankingState) {
    withLoadingSpinner(state.isFetching) {
        flexPanel(direction = FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
            state.combinedScores?.let { combinedScores ->
                scoreRankingTable(
                    headerNames = listOf(StringResources.RANK, StringResources.ALGORITHM, StringResources.AVERAGE),
                    title = StringResources.COMBINED_RANKING_TABLE_HEADER,
                    scores = combinedScores
                )
            }
            divider()
            h5(StringResources.PER_FUNCTION)
            flexPanel(direction = FlexDirection.ROW) {
                val sortedDimensions = state.scores?.keys?.sorted()
                sortedDimensions?.forEach { dimension ->
                    val sortedFunctionNumbers = state.scores[dimension]?.keys?.sorted()
                    flexPanel(direction = FlexDirection.COLUMN) {
                        sortedFunctionNumbers?.forEach { functionNumber ->
                            state.scores[dimension]?.get(functionNumber)?.let { scores ->
                                scoreRankingTable(
                                    headerNames = listOf(StringResources.ALGORITHM, StringResources.RANK),
                                    title = StringResources.DIMENSION_FUNCTION_NUMBER_EQUALS(dimension, functionNumber),
                                    scores = scores
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
