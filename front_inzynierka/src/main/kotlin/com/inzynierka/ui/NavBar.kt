package com.inzynierka.ui

import com.inzynierka.domain.core.MainAppAction
import com.inzynierka.domain.core.Tab
import com.inzynierka.domain.core.UserData
import com.inzynierka.ui.StringResources.ADMIN_CONSOLE_LABEL
import com.inzynierka.ui.StringResources.BROWSE_RANKINGS_LABEL
import com.inzynierka.ui.StringResources.LOGIN_LABEL
import com.inzynierka.ui.StringResources.NAVBAR_TITLE
import com.inzynierka.ui.StringResources.UPLOAD_RESULTS_LABEL
import io.kvision.core.Container
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.navbar.nav
import io.kvision.navbar.navbar
import io.kvision.utils.px

fun Container.navBar(isLoggedIn: Boolean, userData: UserData?, activeTab: Tab) {
    navbar(NAVBAR_TITLE) {
        nav(rightAlign = true) {
            div {
                padding = 4.px
                button(UPLOAD_RESULTS_LABEL, style = tabButtonStyle(activeTab is Tab.Upload)) {
                }.onClick {
                    AppManager.store.dispatch(MainAppAction.TabSelected(Tab.Upload))
                }
            }
            div {
                padding = 4.px
                button(BROWSE_RANKINGS_LABEL, style = tabButtonStyle(activeTab is Tab.ResultsTab)).onClick {
                    AppManager.store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.Cec2022))
                    AppManager.loadCec2022Scores()
                }
            }
            if (userData?.isUserAdmin == true) {
                div {
                    padding = 4.px
                    button(ADMIN_CONSOLE_LABEL, style = tabButtonStyle(activeTab is Tab.AdminConsole)).onClick {
                        AppManager.store.dispatch(MainAppAction.TabSelected(Tab.AdminConsole))
                        AppManager.loadAdminConsole()
                    }
                }
            }
            if (isLoggedIn && userData?.disabled == true) {
                div {
                    padding = 4.px
                    button("VERIFY ACCOUNT", style = tabButtonStyle(activeTab is Tab.Login)).onClick {
                        AppManager.store.dispatch(MainAppAction.TabSelected(Tab.Login))
                    }
                }
            }

            if (!isLoggedIn) {
                div {
                    padding = 4.px
                    button(LOGIN_LABEL, style = tabButtonStyle(activeTab is Tab.Login)).onClick {
                        AppManager.store.dispatch(MainAppAction.TabSelected(Tab.Login))
                    }
                }
            }
        }
    }
}