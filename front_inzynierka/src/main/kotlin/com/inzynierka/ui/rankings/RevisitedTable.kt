package com.inzynierka.ui.rankings

import com.inzynierka.domain.models.RevisitedRankingEntry
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
import io.kvision.utils.toFixedNoRound


fun Container.revisitedTable(headerNames: List<String>, title: String, scores: List<RevisitedRankingEntry>) {
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
                    cell(it.successfulTrialsPercentage.toPercentage(3))
                    cell(it.thresholdsAchievedPercentage.toPercentage(3))
                    cell(it.budgetLeftPercentage.toPercentage(3))
                    cell(it.score.toFixedNoRound(3))
                }
            }
        }
    }
}

fun Double.toPercentage(precision: Int): String {
    return (this * 100).toFixedNoRound(precision) + "%"
}