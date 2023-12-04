package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.*
import com.inzynierka.domain.models.RankingType
import com.inzynierka.model.EcdfData
import com.inzynierka.ui.*
import com.inzynierka.ui.StringResources.AVERAGED
import com.inzynierka.ui.StringResources.DIMENSION_FUNCTION_COMBINED
import com.inzynierka.ui.StringResources.DIMENSION_FUNCTION_GROUP_EQUALS
import com.inzynierka.ui.StringResources.DIMENSION_FUNCTION_NUMBER_EQUALS
import com.inzynierka.ui.StringResources.ECDF_X_AXIS_LABEL
import com.inzynierka.ui.StringResources.ECDF_Y_AXIS_LABEL
import com.inzynierka.ui.StringResources.PER_FUNCTION
import io.kvision.chart.*
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.html.Align
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.utils.obj

private const val DIM_10_MAX_FES = 200_000
private const val DIM_20_MAX_FES = 1_000_000
private val DIM_MAX_FES = mapOf(
    10 to DIM_10_MAX_FES,
    20 to DIM_20_MAX_FES
)

private const val CHART_WIDTH = 600
private const val CHART_HEIGHT = 400

fun Container.ecdfTab(state: EcdfState) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER, spacing = 16) {
        h5(StringResources.ECDF_DESCRIPTION)
        flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER, spacing = 8) {
            button(
                PER_FUNCTION,
                style = tabButtonStyle(state.rankingType is RankingType.PerFunction)
            ).onClick { AppManager.store.dispatch(EcdfAction.EcdfTypeChanged(RankingType.PerFunction)) }
            button(
                AVERAGED,
                style = tabButtonStyle(state.rankingType is RankingType.Averaged)
            ).onClick { AppManager.store.dispatch(EcdfAction.EcdfTypeChanged(RankingType.Averaged)) }
        }
        divider()
        withLoadingSpinner(state.isFetching) {
            when (state.rankingType) {
                is RankingType.PerFunction -> perFunctionEcdfs(state.splitData)
                is RankingType.Averaged -> averagedEcdfs(state.combinedData, state.functionGroupData)
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
        val sortedDimensions = combinedData?.keys?.sorted()
        sortedDimensions?.forEach { dimension ->
            combinedData[dimension]?.let {
                ecdfChart(it, DIMENSION_FUNCTION_COMBINED(dimension), dimension)
            }
        }
    }
    flexPanel(FlexDirection.ROW, alignItems = AlignItems.CENTER) {
        val sortedDimensions = combinedData?.keys?.sorted()
        sortedDimensions?.forEach { dimension ->
            flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
                functionGroupData?.get(dimension)?.forEach { (functionGroup, data) ->
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
                                ?.let { maxFes -> maxFes * 0.001 / dimension },
                            max = DIM_MAX_FES[dimension]?.toDouble()
                                ?.let { maxFes -> maxFes / dimension.toDouble() },
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
