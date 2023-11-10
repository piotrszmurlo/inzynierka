package com.inzynierka.ui

import com.inzynierka.domain.NetworkActions
import com.inzynierka.domain.core.MainAppAction
import com.inzynierka.domain.core.MainAppState
import com.inzynierka.domain.core.Tab
import com.inzynierka.ui.rankings.rankings
import io.kvision.core.Container
import io.kvision.core.Display
import io.kvision.core.JustifyContent
import io.kvision.panel.flexPanel
import io.kvision.redux.ReduxStore
import io.kvision.state.bind
import io.kvision.state.sub

fun Container.mainAppComponent(
    store: ReduxStore<MainAppState, MainAppAction>,
    networkActions: NetworkActions
) {

    navBar(store)
    flexPanel(
        justify = JustifyContent.CENTER
    ).bind(store.sub(extractor = { state -> state.tab })) { tab ->
        display = Display.FLEX
        when (tab) {
            is Tab.Upload -> uploadFileForm(store, networkActions)
            is Tab.ResultsTab -> rankings(store, networkActions)
        }
    }
}