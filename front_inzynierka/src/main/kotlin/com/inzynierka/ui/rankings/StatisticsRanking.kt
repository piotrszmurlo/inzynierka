package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.StatisticsRankingState
import com.inzynierka.ui.withLoadingSpinner
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.JustifyContent
import io.kvision.panel.flexPanel

fun Container.statisticsRanking(
    state: StatisticsRankingState,
    headerNames: List<String>
) {
    withLoadingSpinner(state.isFetching) {
        flexPanel(FlexDirection.ROW, justify = JustifyContent.CENTER, alignItems = AlignItems.CENTER) {
            state.scores?.forEach { dimension ->
                flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
                    dimension.value.forEach { functionNumber ->
                        statisticTable(
                            headerNames = headerNames,
                            title = "Dimension = ${dimension.key}, Function = ${functionNumber.key}",
                            scores = functionNumber.value
                        )
                    }
                }
            }
        }
    }
}