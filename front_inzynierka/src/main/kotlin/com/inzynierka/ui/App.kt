package com.inzynierka.ui

import com.inzynierka.di.appModule
import com.inzynierka.domain.core.Tab
import com.inzynierka.ui.console.adminConsole
import com.inzynierka.ui.login.login
import com.inzynierka.ui.rankings.rankings
import io.kvision.*
import io.kvision.core.JustifyContent
import io.kvision.i18n.DefaultI18nManager
import io.kvision.i18n.I18n
import io.kvision.panel.flexPanel
import io.kvision.panel.root
import io.kvision.state.bind
import org.koin.core.component.KoinComponent
import org.koin.core.context.GlobalContext.startKoin


class App : Application(), KoinComponent {

    init {
        require("css/kvapp.css")
    }

    override fun start() {
        I18n.language = "en"
        I18n.manager = DefaultI18nManager(
            mapOf(
                "en" to require("i18n/messages-en.json"),
            )
        )
        root("kvapp").bind(AppManager.store) { state ->
            navBar(state.isUserLoggedIn, state.loginState.loggedInUserData, state.tab)
            flexPanel(
                justify = JustifyContent.CENTER
            ) {
                when (state.tab) {
                    is Tab.Upload -> {
                        uploadFileForm(state.uploadFilesState)
                    }

                    is Tab.ResultsTab -> {
                        rankings(state.rankingsState, state.tab)
                    }

                    is Tab.AdminConsole -> {
                        adminConsole(state.adminConsoleState)
                    }

                    is Tab.Login -> {
                        login(state.loginState)
                    }
                }
            }
        }
    }
}

fun main() {
    startKoin {
        modules(appModule)
    }

    startApplication(::App, module.hot, BootstrapModule, BootstrapCssModule, CoreModule, ChartModule)
}
