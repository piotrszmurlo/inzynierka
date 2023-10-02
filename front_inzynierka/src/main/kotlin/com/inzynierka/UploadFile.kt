package com.inzynierka

import com.inzynierka.domain.MainAppAction
import com.inzynierka.domain.MainAppState
import com.inzynierka.domain.Result
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
import io.kvision.toast.Toast
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
                        MainAppAction.UploadFormOnChangeHandler(
                            getData().fileToUpload?.get(0)
                        )
                    )
                }
            }
            add(UploadFileForm::fileToUpload, upload(multiple = true))
        }
        val uploadFileButton = button("upload file").bind(store) { state ->
            disabled = state.uploadButtonDisabled
        }
        uploadFileButton.onClick {
            store.dispatch { dispatch, _ ->
                dispatch(MainAppAction.UploadFileStarted)
                CoroutineScope(Dispatchers.Default).launch {
                    uploadFileForm.form.getDataWithFileContent().fileToUpload?.let { files ->
                        when (val result = dataService.postFiles(files)) {
                            is Result.Success -> {
                                Toast.info("File upload completed")
                                dispatch(MainAppAction.UploadFileSuccess)
                            }

                            is Result.Error -> {
                                dispatch(MainAppAction.UploadFileFailed(result.domainError))
                                Toast.info("File upload failed")
                            }
                        }
                    }
                }
            }
        }

    }
}