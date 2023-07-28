package com.inzynierka

import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.inzynierka.data.DomainError
import com.inzynierka.di.appModule
import com.inzynierka.domain.MainAppAction
import com.inzynierka.domain.MainAppState
import com.inzynierka.domain.Tab
import com.inzynierka.domain.mainAppReducer
import com.inzynierka.domain.service.IDataService
import io.kvision.*
import io.kvision.chart.ChartType
import io.kvision.chart.Configuration
import io.kvision.chart.DataSets
import io.kvision.chart.chart
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.UNIT
import io.kvision.core.onChangeLaunch
import io.kvision.form.formPanel
import io.kvision.form.getDataWithFileContent
import io.kvision.form.upload.upload
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.navbar.nav
import io.kvision.navbar.navbar
import io.kvision.panel.root
import io.kvision.panel.simplePanel
import io.kvision.panel.vPanel
import io.kvision.redux.ReduxStore
import io.kvision.redux.createReduxStore
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.toast.Toast
import io.kvision.toast.ToastContainer
import io.kvision.toast.ToastContainerPosition
import io.kvision.types.KFile
import io.kvision.utils.px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.GlobalContext.startKoin


@Serializable
data class UploadFileForm(
    val fileToUpload: List<KFile>? = null
)

class App : Application(), KoinComponent {

    private val store: ReduxStore<MainAppState, MainAppAction>
    private val dataService: IDataService by inject()

    init {
        require("css/kvapp.css")
        val initialMainAppState = MainAppState(listOf(1, 2, 3, 4), tab = Tab.Results, isFetching = false, false, null)
        store = createReduxStore(::mainAppReducer, initialMainAppState)
    }

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
                        store.dispatch(MainAppAction.TabSelected(Tab.Results))
                    }
                }
            }
        }
    }

    fun Container.uploadFileForm(store: ReduxStore<MainAppState, MainAppAction>) {
        vPanel(alignItems = AlignItems.CENTER) {
            div {
                content = "Select results file to upload"
                padding = 8.px
            }
            val uploadFileForm = formPanel<UploadFileForm> {
                onChangeLaunch {
                    CoroutineScope(Dispatchers.Default).launch {
                        store.dispatch(
                            MainAppAction.UploadFormOnChangeHandler(
                                getData().fileToUpload?.get(0)
                            )
                        )
                    }
                }
                add(UploadFileForm::fileToUpload, upload { })
            }
            val uploadFileButton = button("upload file").bind(store) { state ->
                disabled = state.uploadButtonDisabled
            }
            uploadFileButton.onClick {
                store.dispatch { dispatch, _ ->
                    dispatch(MainAppAction.UploadFileStarted)
                    CoroutineScope(Dispatchers.Default).launch {
                        uploadFileForm.form.getDataWithFileContent().fileToUpload?.get(0)?.let { file ->
                            dataService.postFile(file)
                                .onSuccess {
                                    Toast.info("File upload completed")
                                    dispatch(MainAppAction.UploadFileSuccess)
                                }
                                .onFailure {
                                    dispatch(MainAppAction.UploadFileFailed(it))
                                    Toast.info("File upload failed")
                                }
                        }
                    }
                }
            }
        }
    }

    fun Container.results(store: ReduxStore<MainAppState, MainAppAction>) {
        simplePanel().bind(store.sub(extractor = { state -> state.data })) { state ->
            height = Pair(550, UNIT.px)
            width = Pair(550, UNIT.px)
            chart(
                Configuration(
                    ChartType.BAR,
                    listOf(DataSets(data = state)),
                    listOf("One", "Two", "Three", "Four")
                )
            )
        }
    }

    override fun start() {
        root("kvapp") {
            navBar(store)

            div().bind(store) { state ->
                when (state.tab) {
                    is Tab.Upload -> uploadFileForm(store)
                    is Tab.Results -> results(store)
                }
            }

            div().bind(store) { state ->
                state.error?.let {
                    val toastContainer = ToastContainer(ToastContainerPosition.TOPCENTER)
                    toastContainer.showToast(
                        "Some information about the error",
                        "Error"
                    )
                    store.dispatch(MainAppAction.ErrorHandled)
                }
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

                            .onSuccess {
                                dispatch(MainAppAction.FetchDataSuccess(it.data))
                                Toast.info("Data fetch completed")
                            }
                            .onFailure { dispatch(MainAppAction.FetchDataFailed(it)) }
                    }
                }
            }
            button("Mock fetch data error").onClick {
                store.dispatch(MainAppAction.FetchDataFailed(DomainError.NetworkError("Mock error")))
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
