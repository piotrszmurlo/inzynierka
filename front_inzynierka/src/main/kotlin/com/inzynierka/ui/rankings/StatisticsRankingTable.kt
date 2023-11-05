package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.NumberNotation
import com.inzynierka.domain.models.StatisticsRankingEntry
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.JustifyContent
import io.kvision.html.Align
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.table.TableType
import io.kvision.table.cell
import io.kvision.table.row
import io.kvision.table.table
import io.kvision.utils.px
import js.core.toExponential

fun Container.statisticTable(
    headerNames: List<String>,
    title: String,
    scores: List<StatisticsRankingEntry>,
    notation: NumberNotation,
    precision: Int
) {
    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
        padding = 16.px
        h5(content = title, align = Align.CENTER)
        table(
            headerNames = headerNames,
            types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
        ) {
            scores.forEach {
                row {
                    cell("${it.rank}")
                    cell(it.algorithmName)
                    cell(it.mean.format(notation, precision))
                    cell(it.median.format(notation, precision))
                    cell(it.stdev.format(notation, precision))
                    cell(it.min.format(notation, precision))
                    cell(it.max.format(notation, precision))
                }
            }
        }
    }
}

fun Double.format(notation: NumberNotation, precision: Int): String {
    return when (notation) {
        is NumberNotation.Decimal -> {
            this.toExponential(precision).toDouble().toString()
        }

        is NumberNotation.Scientific -> {
            this.toExponential(precision)
        }
    }
}
