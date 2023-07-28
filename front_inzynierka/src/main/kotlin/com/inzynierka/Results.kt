package com.inzynierka

import com.inzynierka.domain.MainAppAction
import com.inzynierka.domain.MainAppState
import io.kvision.chart.ChartType
import io.kvision.chart.Configuration
import io.kvision.chart.DataSets
import io.kvision.chart.chart
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.panel.vPanel
import io.kvision.redux.ReduxStore
import io.kvision.state.bind
import io.kvision.state.sub

fun Container.results(store: ReduxStore<MainAppState, MainAppAction>) {
    vPanel().bind(store.sub(extractor = { state -> state.data })) { state ->
        alignItems = AlignItems.CENTER
        chart(
            Configuration(
                ChartType.BAR,
                listOf(DataSets(data = state)),
                listOf("One", "Two", "Three", "Four")
            ),
            600, 400
        )
    }
}