package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.StatisticsRankingState
import com.inzynierka.ui.StringResources.DIMENSION_FUNCTION_NUMBER_EQUALS
import com.inzynierka.ui.StringResources.SELECT_PRECISION_AND_NOTATION
import com.inzynierka.ui.StringResources.TOAST_FAILED_TO_LOAD_RANKING
import com.inzynierka.ui.StringResources.TOGGLE_NOTATION
import com.inzynierka.ui.divider
import com.inzynierka.ui.show
import com.inzynierka.ui.withLoadingSpinner
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.onChange
import io.kvision.form.select.select
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.toast.Toast

fun Container.statisticsRanking(
    state: StatisticsRankingState,
    headerNames: List<String>,
    onHandleError: () -> Unit,
    toggleNumberNotation: () -> Unit,
    changePrecision: (Int) -> Unit,
) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER, spacing = 8) {
        h5(content = SELECT_PRECISION_AND_NOTATION)
        select(
            options = state.availablePrecisions.map { it.toString() to it.toString() },
            value = state.numberPrecision.toString()
        )
            .onChange {
                changePrecision(this.value?.toInt()!!)
            }

        button(text = TOGGLE_NOTATION, style = ButtonStyle.OUTLINEPRIMARY)
            .onClick {
                toggleNumberNotation()
            }
        divider()
        withLoadingSpinner(state.isFetching) {
            flexPanel(FlexDirection.ROW) {
                val sortedDimensions = state.scores?.keys?.sorted()
                sortedDimensions?.forEach { dim ->
                    flexPanel(FlexDirection.COLUMN) {
                        val sortedFunctionNumbers = state.scores[dim]?.keys?.sorted()
                        sortedFunctionNumbers?.forEach { functionNumber ->
                            state.scores[dim]?.get(functionNumber)?.let { scores ->
                                statisticTable(
                                    headerNames = headerNames,
                                    title = DIMENSION_FUNCTION_NUMBER_EQUALS(dim, functionNumber),
                                    scores = scores,
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
    state.error?.let {
        Toast.show(TOAST_FAILED_TO_LOAD_RANKING)
        onHandleError()
    }
}