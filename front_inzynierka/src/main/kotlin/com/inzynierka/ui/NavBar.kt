package com.inzynierka.ui

import com.inzynierka.domain.core.MainAppAction
import com.inzynierka.domain.core.Tab
import io.kvision.core.Container
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.navbar.nav
import io.kvision.navbar.navbar
import io.kvision.utils.px

fun Container.navBar() {
    navbar("AE comparison") {
        nav(rightAlign = true) {
            div {
                padding = 4.px
                button("Upload result").onClick {
                    AppManager.store.dispatch(MainAppAction.TabSelected(Tab.Upload))
                }
            }
            div {
                padding = 4.px
                button("Browse rankings").onClick {
                    AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Cec2022))
                }
            }
        }
    }
}