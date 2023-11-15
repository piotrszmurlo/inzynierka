package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.StatisticsRankingState
import com.inzynierka.ui.StringResources.DIMENSION_FUNCTION_NUMBER_EQUALS
import com.inzynierka.ui.StringResources.SELECT_PRECISION_AND_NOTATION
import com.inzynierka.ui.StringResources.TOGGLE_NOTATION
import com.inzynierka.ui.withLoadingSpinner
import io.kvision.core.*
import io.kvision.form.select.select
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel

fun Container.statisticsRanking(
    state: StatisticsRankingState,
    headerNames: List<String>,
    toggleNumberNotation: () -> Unit,
    changePrecision: (Int) -> Unit
) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER, spacing = 8) {
        h5(content = SELECT_PRECISION_AND_NOTATION)
        select(
            options = listOf("1" to "1", "3" to "3", "5" to "5", "8" to "8"),
            value = state.numberPrecision.toString()
        )
            .onChange {
                changePrecision(this.value?.toInt()!!)
            }

        button(text = TOGGLE_NOTATION, style = ButtonStyle.OUTLINEPRIMARY)
            .onClick {
                toggleNumberNotation()
            }

        withLoadingSpinner(state.isFetching) {
            flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER) {
                val sortedDimensions = state.scores?.keys?.sorted()
                sortedDimensions?.forEach { dim ->
                    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
                        state.scores[dim]?.forEach { functionNumber ->
                            statisticTable(
                                headerNames = headerNames,
                                title = DIMENSION_FUNCTION_NUMBER_EQUALS(dim, functionNumber.key),
                                scores = functionNumber.value,
                                notation = state.numberNotation,
                                precision = state.numberPrecision
                            )
                        }
                    }
                }
            }
        }
    }
}