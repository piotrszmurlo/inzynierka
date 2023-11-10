package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.RevisitedRankingState
import com.inzynierka.domain.models.RevisitedRankingEntry
import com.inzynierka.ui.toPercentage
import com.inzynierka.ui.withLoadingSpinner
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.JustifyContent
import io.kvision.html.Align
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.table.TableType
import io.kvision.table.cell
import io.kvision.table.row
import io.kvision.table.table
import io.kvision.utils.px
import io.kvision.utils.toFixedNoRound

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

fun Container.revisitedTable(headerNames: List<String>, title: String, scores: List<RevisitedRankingEntry>) {
    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
        padding = 16.px
        h5(content = title, align = Align.CENTER)
        table(
            headerNames = headerNames,
            types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
        ) {
            scores.forEach {
                row {
                    cell("${it.rank}")
                    cell(it.algorithmName)
                    cell(it.successfulTrialsPercentage.toPercentage(3))
                    cell(it.thresholdsAchievedPercentage.toPercentage(3))
                    cell(it.budgetLeftPercentage.toPercentage(3))
                    cell(it.score.toFixedNoRound(3))
                }
            }
        }
    }
}