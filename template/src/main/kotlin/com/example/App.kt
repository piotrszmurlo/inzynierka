package com.example

import com.example.data.DataRepository
import com.example.data.DataService
import com.example.data.DomainError
import com.example.domain.MainAppAction
import com.example.domain.MainAppState
import com.example.domain.mainAppReducer
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import io.kvision.*
import io.kvision.chart.ChartType
import io.kvision.chart.Configuration
import io.kvision.chart.DataSets
import io.kvision.chart.chart
import io.kvision.core.UNIT
import io.kvision.form.upload.upload
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.panel.root
import io.kvision.panel.simplePanel
import io.kvision.redux.ReduxStore
import io.kvision.redux.createReduxStore
import io.kvision.rest.RestClient
import io.kvision.state.bind
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class App : Application() {

    private val restClient: RestClient
    private val store: ReduxStore<MainAppState, MainAppAction>
    private val dataRepository = DataRepository()
    private val dataService = DataService(dataRepository)

    init {
        require("css/kvapp.css")
        restClient = RestClient()
        val initialMainAppState = MainAppState(listOf(1, 2, 3, 4), false, false, null)
        store = createReduxStore(::mainAppReducer, initialMainAppState)
    }

    override fun start() {
        root("kvapp") {
            upload { }
            simplePanel().bind(store) {state ->
                height = Pair(550, UNIT.px)
                width = Pair(550, UNIT.px)
                chart(
                    Configuration(
                        ChartType.BAR,
                        listOf(DataSets(data = state.data)),
                        listOf("One", "Two", "Three", "Four")
                    )
                )
            }
            div().bind(store) { state ->
                div("example: " + state.data)
            }
            div().bind(store) { state ->
                div("started: " + state.isFetching.toString())
            }
            div().bind(store) { state ->
                div("success: " + state.success.toString())
            }
            div().bind(store) { state ->
                div("error: " + state.error as? DomainError.NetworkError)
            }
            button("Fetch data").onClick {
                store.dispatch { dispatch, _ ->
                    dispatch(MainAppAction.FetchDataStarted)
                    GlobalScope.launch {
                        dataService.getData()
                            .onSuccess { dispatch(MainAppAction.FetchDataSuccess(it.data)) }
                            .onFailure { dispatch(MainAppAction.FetchDataFailed(it)) }
                    }
                }
            }

        }
    }
}

fun main() {
    startApplication(::App, module.hot, BootstrapModule, BootstrapCssModule, CoreModule, ChartModule, ReduxModule)
}
