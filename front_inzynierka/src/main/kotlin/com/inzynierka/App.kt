package com.inzynierka

import com.inzynierka.di.appModule
import com.inzynierka.domain.MainAppAction
import com.inzynierka.domain.MainAppState
import com.inzynierka.domain.Tab
import com.inzynierka.domain.mainAppReducer
import com.inzynierka.domain.service.IDataService
import io.kvision.*
import io.kvision.panel.root
import io.kvision.redux.ReduxStore
import io.kvision.redux.createReduxStore
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin


class App : Application(), KoinComponent {

    private val store: ReduxStore<MainAppState, MainAppAction>
    private val dataService: IDataService by inject()

    init {
        require("css/kvapp.css")
        val initialMainAppState =
            MainAppState(listOf(1, 2, 3, 4), tab = Tab.Results, isFetching = false, success = false, error = null)
        store = createReduxStore(::mainAppReducer, initialMainAppState)
    }

    override fun start() {
        root("kvapp") {
            mainAppComponent(store, dataService)
        }
    }

}

fun main() {
    startKoin {
        modules(appModule)
    }

    startApplication(::App, module.hot, BootstrapModule, BootstrapCssModule, CoreModule, ChartModule, ReduxModule)
}
