package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.*
import com.inzynierka.model.EcdfData
import com.inzynierka.ui.AppManager
import com.inzynierka.ui.StringResources.AVERAGED
import com.inzynierka.ui.StringResources.DIMENSION_FUNCTION_COMBINED
import com.inzynierka.ui.StringResources.DIMENSION_FUNCTION_GROUP_EQUALS
import com.inzynierka.ui.StringResources.DIMENSION_FUNCTION_NUMBER_EQUALS
import com.inzynierka.ui.StringResources.ECDF_X_AXIS_LABEL
import com.inzynierka.ui.StringResources.ECDF_Y_AXIS_LABEL
import com.inzynierka.ui.StringResources.PER_FUNCTION
import com.inzynierka.ui.divider
import com.inzynierka.ui.tabButtonStyle
import com.inzynierka.ui.withLoadingSpinner
import io.kvision.chart.*
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.html.Align
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

private const val CHART_WIDTH = 600
private const val CHART_HEIGHT = 400

fun Container.ecdfTab(state: EcdfState) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER, spacing = 8) {
        flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER, spacing = 8) {
            button(
                PER_FUNCTION,
                style = tabButtonStyle(state.ecdfType is EcdfType.PerFunction)
            ).onClick { AppManager.store.dispatch(EcdfAction.EcdfTypeChanged(EcdfType.PerFunction)) }
            button(
                AVERAGED,
                style = tabButtonStyle(state.ecdfType is EcdfType.Averaged)
            ).onClick { AppManager.store.dispatch(EcdfAction.EcdfTypeChanged(EcdfType.Averaged)) }
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
        val sortedDimensions = data?.keys?.sorted()
        sortedDimensions?.forEach { dimension ->
            flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
                val sortedFunctionNumbers = data[dimension]?.keys?.sorted()
                sortedFunctionNumbers?.forEach { functionNumber ->
                    val title = DIMENSION_FUNCTION_NUMBER_EQUALS(dimension, functionNumber)
                    data[dimension]?.get(functionNumber)?.let { ecdfData ->
                        ecdfChart(ecdfData, title, dimension)
                        divider()
                    }
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
            ecdfChart(data, DIMENSION_FUNCTION_COMBINED(dimension), dimension)
        }
    }
    flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER) {
        functionGroupData?.forEach { (dimension, functionGroupsData) ->
            flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
                functionGroupsData.forEach { (functionGroup, data) ->
                    ecdfChart(
                        data,
                        DIMENSION_FUNCTION_GROUP_EQUALS(dimension, functionGroup.name.lowercase()),
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
                                ?.let { maxFes -> log10(maxFes / dimension.toDouble()) },
                            title = ScaleTitleOptions(
                                display = true,
                                text = ECDF_X_AXIS_LABEL,
                                font = ChartFont(size = 20)
                            )
                        ),
                        "y" to ChartScales(
                            min = 0,
                            max = 1,
                            title = ScaleTitleOptions(
                                display = true,
                                text = ECDF_Y_AXIS_LABEL,
                                font = ChartFont(size = 20)
                            )
                        )
                    ),
                    animation = AnimationOptions(disabled = true)
                ),
            ),
            chartWidth = CHART_WIDTH,
            chartHeight = CHART_HEIGHT
        )
    }
}
