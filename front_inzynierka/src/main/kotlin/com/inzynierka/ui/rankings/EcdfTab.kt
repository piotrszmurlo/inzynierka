package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.*
import com.inzynierka.domain.models.RankingType
import com.inzynierka.model.EcdfData
import com.inzynierka.ui.*
import com.inzynierka.ui.StringResources.AUC
import com.inzynierka.ui.StringResources.AVERAGED
import com.inzynierka.ui.StringResources.DIMENSION_FUNCTION_COMBINED
import com.inzynierka.ui.StringResources.DIMENSION_FUNCTION_GROUP_EQUALS
import com.inzynierka.ui.StringResources.DIMENSION_FUNCTION_NUMBER_EQUALS
import com.inzynierka.ui.StringResources.ECDF_X_AXIS_LABEL
import com.inzynierka.ui.StringResources.ECDF_Y_AXIS_LABEL
import com.inzynierka.ui.StringResources.PER_FUNCTION
import io.kvision.chart.*
import io.kvision.core.*
import io.kvision.html.Align
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.utils.obj

private const val DIM_10_MAX_FES = 200_000
private const val DIM_20_MAX_FES = 1_000_000
val DIM_MAX_FES = mapOf(
    10 to DIM_10_MAX_FES,
    20 to DIM_20_MAX_FES
)
private const val CHART_WIDTH = 600
private const val CHART_HEIGHT = 400

private val colors = listOf(
    0xFF0000, 0x00FF00, 0x0000FF, 0x4682B4, 0xFF00FF,
    0x00FFFF, 0xFFA500, 0x800080, 0x008000, 0xFFC0CB,
    0xFFD700, 0x00FF7F, 0xFF6347, 0x40E0D0, 0xFFE4E1,
    0xFFFF00, 0x8B4513, 0x7CFC00, 0x9370DB, 0x7B68EE,
    0xFF1493, 0x228B22, 0x800000, 0xFF4500, 0x9400D3,
    0x8A2BE2, 0xA0522D, 0xFFD700, 0x800000, 0x483D8B,
    0xFF69B4, 0xCD5C5C, 0x6A5ACD, 0x98FB98, 0x8B008B,
    0xFF7F50, 0x8B0000, 0x008B8B, 0xFFA07A, 0x32CD32,
    0x20B2AA, 0x00FF00, 0x00FF7F, 0x6B8E23, 0x008000,
    0x3CB371, 0x48D1CC, 0xADFF2F, 0x00FA9A, 0x2E8B57
).map { Color.hex(it) }

private val pointStyles = listOf(
    PointStyle.CIRCLE, PointStyle.RECT, PointStyle.RECTROT, PointStyle.RECTROUNDED, PointStyle.TRIANGLE
)

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
            button(
                AUC,
                style = tabButtonStyle(state.rankingType is RankingType.Area)
            ).onClick { AppManager.store.dispatch(EcdfAction.EcdfTypeChanged(RankingType.Area)) }
        }
        divider()
        withLoadingSpinner(state.isFetching) {
            when (state.rankingType) {
                is RankingType.PerFunction -> perFunctionEcdfs(state.splitData)
                is RankingType.Averaged -> averagedEcdfs(state.combinedData, state.functionGroupData)
                is RankingType.Area -> areaRanking(state)
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
        val data = ecdfDataList.mapIndexed { index, ecdfData ->
            DataSets(
                data = (ecdfData.functionEvaluations zip ecdfData.thresholdAchievedFractions)
                    .map {
                        obj {
                            x = it.first
                            y = it.second
                        }
                    },
                borderColor = listOf(colors[index % colors.size]),
                backgroundColor = listOf(colors[index % colors.size]),
                pointStyle = listOf(pointStyles[index % pointStyles.size]),
                pointRadius = listOf(5),
                pointBorderColor = listOf(Color.name(Col.BLACK)),
                pointHoverRadius = listOf(8),
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
                            ),
                            type = ScalesType.LOGARITHMIC
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

fun Container.areaRanking(state: EcdfState) {
    withLoadingSpinner(state.isFetching) {
        flexPanel(direction = FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
            state.averageRankSplitAreas?.let { combinedScores ->
                scoreRankingTable(
                    headerNames = listOf(StringResources.RANK, StringResources.ALGORITHM, StringResources.AVERAGE),
                    title = StringResources.COMBINED_RANKING_TABLE_HEADER,
                    scores = combinedScores
                )
            }
            divider()
            h5(PER_FUNCTION)
            flexPanel(direction = FlexDirection.ROW) {
                val sortedDimensions = state.splitAreas?.keys?.sorted()
                sortedDimensions?.forEach { dimension ->
                    val sortedFunctionNumbers = state.splitAreas[dimension]?.keys?.sorted()
                    flexPanel(direction = FlexDirection.COLUMN) {
                        sortedFunctionNumbers?.forEach { functionNumber ->
                            state.splitAreas[dimension]?.get(functionNumber)?.let { scores ->
                                scoreRankingTable(
                                    headerNames = listOf(
                                        StringResources.RANK,
                                        StringResources.ALGORITHM,
                                        StringResources.AREA_UNDER_CURVE
                                    ),
                                    title = DIMENSION_FUNCTION_NUMBER_EQUALS(dimension, functionNumber),
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
