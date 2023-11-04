package com.inzynierka.ui.rankings

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

fun Container.statisticTable(
    headerNames: List<String>,
    title: String,
    scores: List<StatisticsRankingEntry>,
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
                    cell("${it.mean}")
                    cell("${it.median}")
                    cell("${it.stdev}")
                    cell("${it.min}")
                    cell("${it.max}")
                }
            }
        }
    }
}
