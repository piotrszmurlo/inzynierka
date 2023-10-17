package com.inzynierka.rankings

import com.inzynierka.domain.MainAppAction
import com.inzynierka.domain.MainAppState
import com.inzynierka.domain.service.IDataService
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.JustifyContent
import io.kvision.html.Align
import io.kvision.html.p
import io.kvision.panel.flexPanel
import io.kvision.redux.ReduxStore
import io.kvision.table.TableType
import io.kvision.table.cell
import io.kvision.table.row
import io.kvision.table.table
import io.kvision.utils.px

fun Container.cec2022(store: ReduxStore<MainAppState, MainAppAction>, dataService: IDataService) {
    flexPanel(direction = FlexDirection.ROW) {
        justifyContent = JustifyContent.CENTER
        rankingTable()
        rankingTable()
    }
}

fun Container.rankingTable() {
    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
        padding = 16.px
        p(content = "123", align = Align.CENTER)
        table(
            headerNames = listOf("Rank", "Algorithm", "CEC 2022 score"),
            types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
        ) {
            row {
                cell("1")
                cell("IOMEAIU")
                cell("123")
            }
        }
    }
}