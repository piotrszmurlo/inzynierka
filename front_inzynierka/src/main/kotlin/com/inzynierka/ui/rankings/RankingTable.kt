package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.NumberNotation
import com.inzynierka.domain.models.ScoreRankingEntry
import com.inzynierka.ui.format
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

private const val SCORE_PRECISION = 6
fun Container.scoreRankingTable(headerNames: List<String>, title: String, scores: List<ScoreRankingEntry>) {
    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
        padding = 16.px
        h5(content = title, align = Align.CENTER)
        table(
            headerNames = headerNames,
            types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
        ) {
            scores.forEach {
                row {
                    it.rank?.let { rank -> cell("$rank") }
                    cell(it.algorithmName)
                    cell(it.score.format(NumberNotation.Decimal, SCORE_PRECISION))
                }
            }
        }
    }
}
