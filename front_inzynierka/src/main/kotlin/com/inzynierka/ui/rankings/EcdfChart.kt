package com.inzynierka.ui.rankings

import com.inzynierka.model.EcdfData
import io.kvision.chart.*
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.html.Align
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.utils.obj


fun Container.ecdfChart(ecdfDataList: List<EcdfData>, title: String) {
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
                        "x" to ChartScales(min = 0, max = 5),
                        "y" to ChartScales(min = 0, max = 1)
                    ),
                    animation = AnimationOptions(disabled = true)
                ),
            ),
            600, 400
        )
    }
}
