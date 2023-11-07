package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.EcdfState
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.panel.flexPanel

fun Container.ecdf(state: EcdfState) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
        state.combinedData?.forEach { (dimension, data) ->
            ecdfChart(data, "Dimension = $dimension, combined functions")
        }
        flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER) {
            state.data?.forEach { (dimension, entries) ->
                flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
                    entries.forEach { (functionNumber, ecdfData) ->
                        val title = "Dimension = $dimension, Function Number = $functionNumber"
                        ecdfChart(ecdfData, title)
                    }
                }
            }
        }
    }
}
