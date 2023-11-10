package com.inzynierka.ui

import com.inzynierka.domain.NetworkActions
import com.inzynierka.domain.core.MainAppAction
import com.inzynierka.domain.core.MainAppState
import com.inzynierka.domain.core.Tab
import com.inzynierka.domain.core.UploadAction
import com.inzynierka.ui.rankings.rankings
import io.kvision.core.Container
import io.kvision.core.Display
import io.kvision.core.JustifyContent
import io.kvision.html.div
import io.kvision.panel.flexPanel
import io.kvision.redux.ReduxStore
import io.kvision.state.bind
import io.kvision.state.sub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun Container.mainAppComponent(
    store: ReduxStore<MainAppState, MainAppAction>,
    networkActions: NetworkActions
) {

    navBar(store)
    flexPanel(
        justify = JustifyContent.CENTER
    ).bind(store.sub(extractor = { state -> state.tab })) { tab ->
        display = Display.FLEX
        when (tab) {
            is Tab.Upload -> {
                div().bind(store.sub { state -> state.uploadFilesState }) { state ->
                    uploadFileForm(
                        state,
                        onExcessiveFileSizeError = {
                            store.dispatch(UploadAction.UploadFileFailed(it))
                        },
                        onSubmit = {
                            store.dispatch { dispatch, _ ->
                                CoroutineScope(Dispatchers.Default).launch {
                                    networkActions.uploadFiles(dispatch, it)
                                }
                            }
                        },
                    ) {
                        store.dispatch(
                            UploadAction.UploadFormOnChangeHandler(it)
                        )
                    }
                }
            }

            is Tab.ResultsTab -> rankings(store, networkActions)
        }
    }
}