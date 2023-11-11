package com.inzynierka.ui

import com.inzynierka.di.appModule
import com.inzynierka.domain.core.Tab
import com.inzynierka.ui.rankings.rankings
import io.kvision.*
import io.kvision.core.JustifyContent
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
        root("kvapp") {
            navBar()
            flexPanel(
                justify = JustifyContent.CENTER
            ).bind(AppManager.store) { state ->
                when (state.tab) {
                    is Tab.Upload -> {
                        uploadFileForm(
                            state.uploadFilesState,
                        )
                    }

                    is Tab.ResultsTab -> {
                        rankings(state.rankingsState, state.tab)
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
