package com.inzynierka

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.inzynierka.data.DomainError
import com.inzynierka.di.appModule
import com.inzynierka.domain.MainAppAction
import com.inzynierka.domain.MainAppState
import com.inzynierka.domain.mainAppReducer
import com.inzynierka.domain.service.IDataService
import io.kvision.*
import io.kvision.chart.ChartType
import io.kvision.chart.Configuration
import io.kvision.chart.DataSets
import io.kvision.chart.chart
import io.kvision.core.UNIT
import io.kvision.form.formPanel
import io.kvision.form.getDataWithFileContent
import io.kvision.form.upload.upload
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.panel.root
import io.kvision.panel.simplePanel
import io.kvision.redux.ReduxStore
import io.kvision.redux.createReduxStore
import io.kvision.state.bind
import io.kvision.types.KFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin


@Serializable
data class UploadFileForm(
    val upload: List<KFile>? = null
)


class App : Application(), KoinComponent {

    private val store: ReduxStore<MainAppState, MainAppAction>
    private val dataService: IDataService by inject()

    init {
        require("css/kvapp.css")
        val initialMainAppState = MainAppState(listOf(1, 2, 3, 4), false, false, null)
        store = createReduxStore(::mainAppReducer, initialMainAppState)
    }

    override fun start() {
        root("kvapp") {
            val f = formPanel<UploadFileForm> {
                add(UploadFileForm::upload, upload { })
            }
            button("upload file").onClick {
                CoroutineScope(Dispatchers.Default).launch {
                    dataService.postFile(f.getDataWithFileContent().upload!![0])
//                    store.dispatch(MainAppAction.UploadFile(f.getDataJson()))
                }
            }
            simplePanel().bind(store) { state ->
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
                    CoroutineScope(Dispatchers.Default).launch {
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

    startKoin {
        modules(appModule)
    }

    startApplication(::App, module.hot, BootstrapModule, BootstrapCssModule, CoreModule, ChartModule, ReduxModule)
}
