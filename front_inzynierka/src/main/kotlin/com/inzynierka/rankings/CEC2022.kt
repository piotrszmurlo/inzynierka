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

fun Container.cec2022(scores: Scores?, combinedScores: List<Score>?) {
    flexPanel(direction = FlexDirection.ROW) {
        justifyContent = JustifyContent.CENTER
        scores?.forEach {
            rankingTable(
                headerNames = listOf("Rank", "Algorithm", "CEC 2022 score"),
                title = "Dimension = ${it.key}",
                scores = it.value
            )
        }
        combinedScores?.let {
            rankingTable(
                headerNames = listOf("Rank", "Algorithm", "combined CEC 2022 score"),
                title = "Combined ranking",
                scores = it
            )
        }
    }
}

fun Container.rankingTable(headerNames: List<String>, title: String, scores: List<Score>) {
    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
        padding = 16.px
        p(content = title, align = Align.CENTER)
        table(
            headerNames = headerNames,
            types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
        ) {
            scores.forEach {
                row {
                    cell("${it.rank}")
                    cell(it.algorithmName)
                    cell("${it.score}")
                }
            }
        }
    }
}