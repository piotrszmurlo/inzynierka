package com.inzynierka.ui

import com.inzynierka.common.DomainError
import com.inzynierka.domain.core.UploadFilesState
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.onChangeLaunch
import io.kvision.form.formPanel
import io.kvision.form.getDataWithFileContent
import io.kvision.form.upload.upload
import io.kvision.html.button
import io.kvision.html.div
import io.kvision.panel.vPanel
import io.kvision.toast.Toast
import io.kvision.types.KFile
import io.kvision.utils.px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

const val MAX_UPLOAD_SIZE = 200_000

@Serializable
private data class UploadFileForm(
    val fileToUpload: List<KFile>? = null
)

fun Container.uploadFileForm(
    state: UploadFilesState,
    onExcessiveFileSizeError: (DomainError) -> Unit,
    onSubmit: (List<KFile>) -> Unit,
    onFilesChanged: (KFile?) -> Unit
) {
    vPanel(alignItems = AlignItems.CENTER) {
        div {
            content = "Select results file to upload"
            padding = 8.px
        }
        val uploadFileForm = formPanel<UploadFileForm> {
            onChangeLaunch {
                CoroutineScope(Dispatchers.Default).launch {
                    onFilesChanged(getData().fileToUpload?.getOrNull(0))
                }
            }
            add(UploadFileForm::fileToUpload, upload(multiple = true))
        }

        val uploadFileButton = button("upload file") {
            disabled = state.uploadButtonDisabled || state.isUploading
        }
        uploadFileButton.onClick {
            uploadFileForm.getData().fileToUpload?.sumOf { it.size }?.let { totalSize ->
                if (totalSize > MAX_UPLOAD_SIZE) {
                    onExcessiveFileSizeError(
                        DomainError.FileUploadError(
                            "File size exceeded"
                        )
                    )
                    Toast.show("Maximum file size exceeded")
                } else {
                    CoroutineScope(Dispatchers.Default).launch {
                        uploadFileForm.form.getDataWithFileContent().fileToUpload?.let { files ->
                            onSubmit(files)

                        }
                    }
                }
            }
        }
    }
}