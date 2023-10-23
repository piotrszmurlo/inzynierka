package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.Cec2022RankingState
import com.inzynierka.domain.core.Score
import com.inzynierka.ui.withLoadingSpinner
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

fun Container.cec2022(cec2022RankingState: Cec2022RankingState) {
    withLoadingSpinner(cec2022RankingState.isFetching) {
        flexPanel(direction = FlexDirection.ROW) {
            justifyContent = JustifyContent.CENTER
            cec2022RankingState.cec2022Scores?.forEach {
                rankingTable(
                    headerNames = listOf("Rank", "Algorithm", "CEC 2022 score"),
                    title = "Dimension = ${it.key}",
                    scores = it.value
                )
            }
            cec2022RankingState.cec2022ScoresCombined?.let {
                rankingTable(
                    headerNames = listOf("Rank", "Algorithm", "combined CEC 2022 score"),
                    title = "Combined ranking",
                    scores = it
                )
            }
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