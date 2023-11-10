package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.*
import com.inzynierka.model.EcdfData
import com.inzynierka.ui.withLoadingSpinner
import io.kvision.chart.*
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.html.Align
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.utils.obj
import kotlin.math.log10

private const val DIM_10_MAX_FES = 200_000
private const val DIM_20_MAX_FES = 1_000_000
private val DIM_MAX_FES = mapOf(
    10 to DIM_10_MAX_FES,
    20 to DIM_20_MAX_FES
)

fun Container.ecdfTab(state: EcdfState, perFunctionClicked: () -> Unit, averagedClicked: () -> Unit) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER, spacing = 8) {
        flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER, spacing = 8) {
            button("Per function", style = ButtonStyle.OUTLINEPRIMARY).onClick { perFunctionClicked() }
            button("Averaged", style = ButtonStyle.OUTLINEPRIMARY).onClick { averagedClicked() }
        }
        withLoadingSpinner(state.isFetching) {
            when (state.ecdfType) {
                is EcdfType.PerFunction -> perFunctionEcdfs(state.splitData)
                is EcdfType.Averaged -> averagedEcdfs(state.combinedData, state.functionGroupData)
            }
        }
    }
}

fun Container.perFunctionEcdfs(data: Map<Dimension, Map<FunctionNumber, List<EcdfData>>>?) {
    flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER) {
        data?.forEach { (dimension, entries) ->
            flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
                entries.forEach { (functionNumber, ecdfData) ->
                    val title = "Dimension = $dimension, Function Number = $functionNumber"
                    ecdfChart(ecdfData, title, dimension)
                }
            }
        }
    }
}

fun Container.averagedEcdfs(
    combinedData: Map<Dimension, List<EcdfData>>?,
    functionGroupData: Map<Dimension, Map<FunctionGroup, List<EcdfData>>>?
) {
    flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER) {
        combinedData?.forEach { (dimension, data) ->
            ecdfChart(data, "Dimension = $dimension, combined functions", dimension)
        }
    }
    flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER) {
        functionGroupData?.forEach { (dimension, functionGroupsData) ->
            flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
                functionGroupsData.forEach { (functionGroup, data) ->
                    ecdfChart(
                        data,
                        "Dimension = $dimension, Function Group: ${functionGroup.name.lowercase()}",
                        dimension
                    )
                }
            }
        }
    }
}

fun Container.ecdfChart(ecdfDataList: List<EcdfData>, title: String, dimension: Dimension) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
        h5(content = title, align = Align.CENTER)
        val data = ecdfDataList.map { ecdfData ->
            DataSets(
                data = (ecdfData.functionEvaluations zip ecdfData.thresholdAchievedFractions)
                    .map {
                        obj {
                            x = it.first
                            y = it.second
                        }
                    },
                label = ecdfData.algorithmName
            )
        }

        chart(
            Configuration(
                type = ChartType.SCATTER,
                dataSets = data,
                options = ChartOptions(
                    showLine = true,
                    scales = mapOf(
                        "x" to ChartScales(
                            min = DIM_MAX_FES[dimension]?.toDouble()
                                ?.let { maxFes -> log10(maxFes * 0.001 / dimension) },
                            max = DIM_MAX_FES[dimension]?.toDouble()
                                ?.let { maxFes -> log10(maxFes / dimension.toDouble()) }),
                        "y" to ChartScales(min = 0, max = 1)
                    ),
                    animation = AnimationOptions(disabled = true)
                ),
            ),
            600, 400
        )
    }
}