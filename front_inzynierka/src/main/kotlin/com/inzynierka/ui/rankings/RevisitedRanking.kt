package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.RevisitedRankingState
import com.inzynierka.ui.withLoadingSpinner
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.JustifyContent
import io.kvision.panel.flexPanel

fun Container.revisitedRanking(state: RevisitedRankingState) {
    withLoadingSpinner(state.isFetching) {
        flexPanel(FlexDirection.COLUMN) {
            state.averagedScores?.let {
                revisitedTable(
                    headerNames = listOf(
                        "Rank",
                        "Algorithm name",
                        "Successful trials",
                        "Thresholds achieved",
                        "Budget left",
                        "Score"
                    ), title = "Average", scores = it
                )
            }

            flexPanel(FlexDirection.ROW, justify = JustifyContent.CENTER, alignItems = AlignItems.CENTER) {
                val sortedDimensions = state.scores?.keys?.sorted()
                sortedDimensions?.forEach { dim ->
                    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
                        state.scores[dim]?.forEach { functionNumber ->
                            revisitedTable(
                                headerNames = listOf(
                                    "Rank",
                                    "Algorithm name",
                                    "Successful trials",
                                    "Thresholds achieved",
                                    "Budget left",
                                    "Score"
                                ),
                                title = "Dimension = ${dim}, Function = ${functionNumber.key}",
                                scores = functionNumber.value
                            )
                        }
                    }
                }
            }
        }
    }

}