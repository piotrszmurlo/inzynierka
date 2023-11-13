package com.inzynierka.ui

import com.inzynierka.common.DomainError
import com.inzynierka.domain.core.UploadAction
import com.inzynierka.domain.core.UploadFilesState
import io.kvision.core.*
import io.kvision.form.formPanel
import io.kvision.form.getDataWithFileContent
import io.kvision.form.upload.upload
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.panel.flexPanel
import io.kvision.panel.vPanel
import io.kvision.toast.Toast
import io.kvision.types.KFile
import io.kvision.utils.px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import web.dom.document

const val MAX_UPLOAD_SIZE = 200_000

@Serializable
private data class UploadFileForm(
    val filesToUpload: List<KFile>? = null
)

fun Container.uploadFileForm(
    state: UploadFilesState
) {
    vPanel(alignItems = AlignItems.CENTER) {
        div {
            content = "Select results file to upload"
            padding = 8.px
        }
        formPanel<UploadFileForm> {
            onChangeLaunch {
                getData().filesToUpload?.sumOf { it.size }?.let { totalSize ->
                    if (totalSize > MAX_UPLOAD_SIZE) {
                        AppManager.store.dispatch(
                            UploadAction.UploadFileFailed(
                                DomainError.FileUploadError(
                                    "File size exceeded"
                                )
                            )
                        )
                        Toast.show("Maximum file size exceeded")
                    } else {
                        CoroutineScope(Dispatchers.Default).launch {
                            getDataWithFileContent().filesToUpload?.let { files ->
                                AppManager.store.dispatch(
                                    UploadAction.UploadFormOnChangeHandler(files)
                                )
                            }
                        }
                    }
                }
            }
            val uploadForm = upload(multiple = true) {
                input.id = "UploadFormId"
            }
                .apply { style { display = Display.NONE } }
            add(UploadFileForm::filesToUpload, uploadForm)
            flexPanel(FlexDirection.COLUMN, spacing = 8, alignItems = AlignItems.CENTER) {
                flexPanel(FlexDirection.ROW, spacing = 8) {
                    button("Select files").onClick {
                        uploadForm.input.id?.let { id -> document.getElementById(id)?.click() }
                    }
                    val uploadFileButton = button("upload files") {
                        disabled = state.uploadButtonDisabled || state.isUploading
                    }
                    uploadFileButton.onClick {
                        state.kFiles?.let { files -> AppManager.uploadFiles(files) }
                    }
                }
                state.selectedFiles?.let {
                    div("Selected Files: ")
                    it.forEach { filename ->
                        div(filename)
                    }
                }
            }
        }
    }
}
