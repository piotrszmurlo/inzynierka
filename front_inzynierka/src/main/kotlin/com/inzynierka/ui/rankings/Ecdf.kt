package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.EcdfState
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.html.button
import io.kvision.panel.flexPanel

fun Container.ecdf(state: EcdfState, toggleView: () -> Unit) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER, spacing = 8) {
        button(
            if (state.showFunctionGroups) "Show per function ECDF" else "Show function groups ECDF"
        ).onClick { toggleView() }
        if (state.showFunctionGroups) {
            flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER) {
                state.functionGroupData?.forEach { (dimension, functionGroupsData) ->
                    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
                        functionGroupsData.forEach { (functionGroup, data) ->
                            ecdfChart(data, "Dimension = $dimension, Function Group: ${functionGroup.name.lowercase()}")
                        }
                    }
                }
            }
        } else {
            flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER) {
                state.combinedData?.forEach { (dimension, data) ->
                    ecdfChart(data, "Dimension = $dimension, combined functions")
                }
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
}
