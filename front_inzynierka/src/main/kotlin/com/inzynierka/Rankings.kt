package com.inzynierka

import com.inzynierka.domain.MainAppAction
import com.inzynierka.domain.MainAppState
import com.inzynierka.domain.Tab
import com.inzynierka.domain.service.IDataService
import com.inzynierka.rankings.cec2022
import com.inzynierka.rankings.ecdf
import io.kvision.core.Container
import io.kvision.core.Display
import io.kvision.core.FlexDirection
import io.kvision.core.JustifyContent
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.panel.flexPanel
import io.kvision.redux.ReduxStore
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.utils.px

fun Container.rankings(store: ReduxStore<MainAppState, MainAppAction>, dataService: IDataService) {
    flexPanel(direction = FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
        display = Display.FLEX
        flexPanel(direction = FlexDirection.ROW, justify = JustifyContent.CENTER, spacing = 8) {
            padding = 16.px
            paddingBottom = 32.px
            
            button(text = "CEC 2022", style = ButtonStyle.OUTLINEPRIMARY)
                .onClick {
                    store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.CEC2022))
                }
            button(text = "Mean", style = ButtonStyle.OUTLINEPRIMARY)
                .onClick {
                    store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Mean))
                }
            button(text = "Median", style = ButtonStyle.OUTLINEPRIMARY)
                .onClick {
                    store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Median))
                }
            button(text = "ECDF", style = ButtonStyle.OUTLINEPRIMARY)
                .onClick {
                    store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.ECDF))
                }
            button(text = "Friedman", style = ButtonStyle.OUTLINEPRIMARY)
                .onClick {
                    store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Friedman))
                }
            button("Compare two algorithms", style = ButtonStyle.OUTLINEPRIMARY)
                .onClick {
                    store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.PairTest))
                }
        }

        flexPanel(justify = JustifyContent.CENTER).bind(store.sub(extractor = { state -> state.tab })) { tab ->
            display = Display.FLEX
            when (tab as? Tab.ResultsTab) {
                is Tab.ResultsTab.CEC2022 -> cec2022(store, dataService)
                is Tab.ResultsTab.Friedman -> {}
                is Tab.ResultsTab.Mean -> {}
                is Tab.ResultsTab.Median -> {}
                is Tab.ResultsTab.PairTest -> {}
                is Tab.ResultsTab.ECDF -> ecdf(store, dataService)
                null -> {}
            }
        }
    }
}