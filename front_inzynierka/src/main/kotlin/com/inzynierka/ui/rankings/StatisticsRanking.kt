package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.StatisticsRankingState
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
        h5(content = "Select precision and notation")
        select(
            options = listOf("1" to "1", "3" to "3", "5" to "5", "8" to "8"),
            value = state.numberPrecision.toString()
        )
            .onChange {
                changePrecision(this.value?.toInt()!!)
            }

        button(text = "Toggle notation", style = ButtonStyle.OUTLINEPRIMARY)
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
                                title = "Dimension = ${dim}, Function = ${functionNumber.key}",
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