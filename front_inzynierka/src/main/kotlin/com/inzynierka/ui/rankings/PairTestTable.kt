package com.inzynierka.ui.rankings

import com.inzynierka.domain.models.PairTestEntry
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.JustifyContent
import io.kvision.html.Align
import io.kvision.html.p
import io.kvision.panel.flexPanel
import io.kvision.table.TableType
import io.kvision.table.cell
import io.kvision.table.row
import io.kvision.table.table
import io.kvision.utils.px

fun Container.pairTestTable(
    headerNames: List<String>,
    title: String,
    results: List<PairTestEntry>
) {
    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
        padding = 16.px
        p(content = title, align = Align.CENTER)
        table(
            headerNames = headerNames,
            types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
        ) {
            results.forEach {
                row {
                    cell("${it.functionNumber}")
                    cell(it.winner ?: "Equal")
                }
            }
        }
    }
}

fun Container.pairTestSumTable(
    headerNames: List<String>,
    title: String,
    resultsSum: Map<String, Int>
) {
    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
        padding = 16.px
        p(content = title, align = Align.CENTER)
        table(
            headerNames = headerNames,
            types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
        ) {
            resultsSum.forEach { (algorithmName, sumOfWins) ->
                row {
                    cell(algorithmName)
                    cell("$sumOfWins")
                }
            }
        }
    }
}