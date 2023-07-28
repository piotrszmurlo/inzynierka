package com.inzynierka

import com.inzynierka.domain.MainAppAction
import com.inzynierka.domain.MainAppState
import com.inzynierka.domain.Tab
import com.inzynierka.domain.service.IDataService
import io.kvision.core.Container
import io.kvision.html.div
import io.kvision.redux.ReduxStore
import io.kvision.state.bind
import io.kvision.state.sub
import io.kvision.toast.ToastContainer
import io.kvision.toast.ToastContainerPosition

fun Container.mainAppComponent(store: ReduxStore<MainAppState, MainAppAction>, dataService: IDataService) {
    navBar(store)

    div().bind(store.sub(extractor = { state -> state.tab })) { tab ->
        when (tab) {
            is Tab.Upload -> uploadFileForm(store, dataService)
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
}