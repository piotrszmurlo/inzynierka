package com.inzynierka.rankings

import com.inzynierka.domain.Score
import com.inzynierka.domain.Scores
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

fun Container.cec2022(scores: Scores?) {
    flexPanel(direction = FlexDirection.ROW) {
        justifyContent = JustifyContent.CENTER
        scores?.score?.forEach {
            rankingTable(dimension = it.key, scores = it.value)
        }
    }
}

fun Container.rankingTable(dimension: Int, scores: List<Score>) {
    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
        padding = 16.px
        p(content = "Dimension = $dimension", align = Align.CENTER)
        table(
            headerNames = listOf("Rank", "Algorithm", "CEC 2022 score"),
            types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
        ) {
            scores.forEach {
                row {
                    cell("${it.rank}")
                    cell(it.algorithmName)
                    cell(it.score.toString())
                }
            }
        }
    }
}