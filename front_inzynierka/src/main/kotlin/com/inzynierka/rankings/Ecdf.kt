package com.inzynierka.rankings

import com.inzynierka.domain.MainAppAction
import com.inzynierka.domain.MainAppState
import com.inzynierka.domain.service.IDataService
import io.kvision.chart.ChartType
import io.kvision.chart.Configuration
import io.kvision.chart.DataSets
import io.kvision.chart.chart
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.panel.flexPanel
import io.kvision.redux.ReduxStore
import io.kvision.state.bind
import io.kvision.state.sub


fun Container.ecdf(store: ReduxStore<MainAppState, MainAppAction>, dataService: IDataService) {
    flexPanel().bind(store.sub(extractor = { state -> state.error })) { state ->
        alignItems = AlignItems.CENTER
        chart(
            Configuration(
                ChartType.BAR,
                listOf(DataSets(data = listOf(1, 2, 3, 4))),
                listOf("One", "Two", "Three", "Four")
            ),
            600, 400
        )
    }
}
