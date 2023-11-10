package com.inzynierka.ui

import com.inzynierka.domain.core.MainAppAction
import com.inzynierka.domain.core.MainAppState
import com.inzynierka.domain.core.Tab
import com.inzynierka.domain.service.IDataService
import com.inzynierka.ui.rankings.rankings
import io.kvision.core.Container
import io.kvision.core.Display
import io.kvision.core.JustifyContent
import io.kvision.html.div
import io.kvision.panel.flexPanel
import io.kvision.redux.ReduxStore
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.toast.ToastContainer
import io.kvision.toast.ToastContainerPosition

fun Container.mainAppComponent(store: ReduxStore<MainAppState, MainAppAction>, dataService: IDataService) {

    navBar(store)
    flexPanel(
        justify = JustifyContent.CENTER
    ).bind(store.sub(extractor = { state -> state.tab })) { tab ->
        display = Display.FLEX
        when (tab) {
            is Tab.Upload -> uploadFileForm(store, dataService)
            is Tab.ResultsTab -> rankings(store, dataService)
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
}