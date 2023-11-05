package com.inzynierka.ui

import com.inzynierka.domain.core.MainAppAction
import com.inzynierka.domain.core.MainAppState
import com.inzynierka.domain.core.Tab
import io.kvision.core.Container
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.navbar.nav
import io.kvision.navbar.navbar
import io.kvision.redux.ReduxStore
import io.kvision.utils.px

fun Container.navBar(store: ReduxStore<MainAppState, MainAppAction>) {
    navbar("AE comparison") {
        nav(rightAlign = true) {
            div {
                padding = 4.px
                button("Upload result").onClick {
                    store.dispatch(MainAppAction.TabSelected(Tab.Upload))
                }
            }
            div {
                padding = 4.px
                button("Browse rankings").onClick {
                    store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Cec2022))
                }
            }
        }
    }
}