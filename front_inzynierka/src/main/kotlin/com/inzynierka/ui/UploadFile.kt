package com.inzynierka.ui

import com.inzynierka.common.DomainError
import com.inzynierka.domain.NetworkActions
import com.inzynierka.domain.core.MainAppAction
import com.inzynierka.domain.core.MainAppState
import com.inzynierka.domain.core.UploadAction
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

const val MAX_UPLOAD_SIZE = 200_000

@Serializable
data class UploadFileForm(
    val fileToUpload: List<KFile>? = null
)

fun Container.uploadFileForm(
    store: ReduxStore<MainAppState, MainAppAction>,
    networkActions: NetworkActions
) {
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
            disabled = state.uploadFilesState.uploadButtonDisabled || state.uploadFilesState.isUploading
        }
        uploadFileButton.onClick {
            uploadFileForm.getData().fileToUpload?.sumOf { it.size }?.let { totalSize ->
                if (totalSize > MAX_UPLOAD_SIZE) {
                    store.dispatch(
                        UploadAction.UploadFileFailed(
                            DomainError.FileUploadError(
                                "File size exceeded"
                            )
                        )
                    )
                    Toast.show("Maximum file size exceeded")
                } else {
                    store.dispatch { dispatch, _ ->
                        CoroutineScope(Dispatchers.Default).launch {
                            uploadFileForm.form.getDataWithFileContent().fileToUpload?.let { files ->
                                networkActions.uploadFiles(dispatch, files)
                            }
                        }
                    }
                }
            }
        }
    }
}