package com.inzynierka.ui

import com.inzynierka.domain.core.MainAppAction
import com.inzynierka.domain.core.MainAppState
import com.inzynierka.domain.core.UploadAction
import com.inzynierka.domain.core.uploadFiles
import com.inzynierka.domain.service.IDataService
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.onChangeLaunch
import io.kvision.form.formPanel
import io.kvision.form.getDataWithFileContent
import io.kvision.form.upload.upload
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.panel.vPanel
import io.kvision.redux.ReduxStore
import io.kvision.state.bind
import io.kvision.types.KFile
import io.kvision.utils.px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

@Serializable
data class UploadFileForm(
    val fileToUpload: List<KFile>? = null
)

fun Container.uploadFileForm(store: ReduxStore<MainAppState, MainAppAction>, dataService: IDataService) {
    vPanel(alignItems = AlignItems.CENTER) {
        div {
            content = "Select results file to upload"
            padding = 8.px
        }
        val uploadFileForm = formPanel<UploadFileForm> {
            onChangeLaunch {
                CoroutineScope(Dispatchers.Default).launch {
                    store.dispatch(
                        UploadAction.UploadFormOnChangeHandler(
                            getData().fileToUpload?.getOrNull(0)
                        )
                    )
                }
            }
            add(UploadFileForm::fileToUpload, upload(multiple = true))
        }

        val uploadFileButton = button("upload file").bind(store) { state ->
            disabled = state.uploadFilesState.uploadButtonDisabled
        }

        uploadFileButton.onClick {
            store.dispatch { dispatch, _ ->
                CoroutineScope(Dispatchers.Default).launch {
                    uploadFileForm.form.getDataWithFileContent().fileToUpload?.let { files ->
                        uploadFiles(dispatch, dataService, files)
                    }
                }
            }
        }
    }
}