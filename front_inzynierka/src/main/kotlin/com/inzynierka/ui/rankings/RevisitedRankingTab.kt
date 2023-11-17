package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.RevisitedRankingState
import com.inzynierka.domain.models.RevisitedRankingEntry
import com.inzynierka.ui.StringResources.ALGORITHM_NAME
import com.inzynierka.ui.StringResources.AVERAGE
import com.inzynierka.ui.StringResources.BUDGET_LEFT
import com.inzynierka.ui.StringResources.DIMENSION_FUNCTION_NUMBER_EQUALS
import com.inzynierka.ui.StringResources.RANK
import com.inzynierka.ui.StringResources.REVISITED_DESCRIPTION
import com.inzynierka.ui.StringResources.SCORE
import com.inzynierka.ui.StringResources.SUCCESSFUL_TRIALS
import com.inzynierka.ui.StringResources.THRESHOLDS_ACHIEVED
import com.inzynierka.ui.divider
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

private const val REVISITED_RANKING_PRECISION = 3

fun Container.revisitedRanking(state: RevisitedRankingState) {
    h5(REVISITED_DESCRIPTION)
    divider()
    withLoadingSpinner(state.isFetching) {
        flexPanel(FlexDirection.COLUMN) {
            state.averagedScores?.let { scores ->
                revisitedTable(
                    headerNames = listOf(
                        RANK,
                        ALGORITHM_NAME,
                        SUCCESSFUL_TRIALS,
                        THRESHOLDS_ACHIEVED,
                        BUDGET_LEFT,
                        SCORE
                    ), title = AVERAGE, scores = scores
                )
            }

            flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER) {
                val sortedDimensions = state.scores?.keys?.sorted()
                sortedDimensions?.forEach { dim ->
                    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
                        val sortedFunctionNumbers = state.scores[dim]?.keys?.sorted()
                        sortedFunctionNumbers?.forEach { functionNumber ->
                            state.scores[dim]?.get(functionNumber)?.let { scores ->
                                revisitedTable(
                                    headerNames = listOf(
                                        RANK,
                                        ALGORITHM_NAME,
                                        SUCCESSFUL_TRIALS,
                                        THRESHOLDS_ACHIEVED,
                                        BUDGET_LEFT,
                                        SCORE
                                    ),
                                    title = DIMENSION_FUNCTION_NUMBER_EQUALS(dim, functionNumber),
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
                    cell(it.successfulTrialsPercentage.toPercentage(REVISITED_RANKING_PRECISION))
                    cell(it.thresholdsAchievedPercentage.toPercentage(REVISITED_RANKING_PRECISION))
                    cell(it.budgetLeftPercentage.toPercentage(REVISITED_RANKING_PRECISION))
                    cell(it.score.toFixedNoRound(REVISITED_RANKING_PRECISION))
                }
            }
        }
    }
}