package com.inzynierka.ui

import com.inzynierka.common.DomainError
import com.inzynierka.domain.core.UploadAction
import com.inzynierka.domain.core.UploadFilesState
import com.inzynierka.ui.StringResources.SELECTED_FILES
import com.inzynierka.ui.StringResources.SELECT_FILES
import com.inzynierka.ui.StringResources.TOAST_MAXIMUM_FILE_SIZE_EXCEEDED
import com.inzynierka.ui.StringResources.UPLOAD_FILES
import com.inzynierka.ui.StringResources.UPLOAD_FILE_TAB_TITLE
import io.kvision.core.*
import io.kvision.form.formPanel
import io.kvision.form.getDataWithFileContent
import io.kvision.form.upload.upload
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.toast.Toast
import io.kvision.types.KFile
import io.kvision.utils.px
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import web.dom.document

private const val MAX_UPLOAD_SIZE = 200_000
private const val FILE_LIST_COLUMN_SIZE = 6

@Serializable
private data class UploadFileForm(
    val filesToUpload: List<KFile>? = null
)

fun Container.uploadFileForm(
    state: UploadFilesState
) {
    flexPanel(
        alignItems = AlignItems.CENTER,
        spacing = 24,
        alignContent = AlignContent.SPACEAROUND,
        direction = FlexDirection.COLUMN
    ) {
        paddingTop = 32.px
        h5(UPLOAD_FILE_TAB_TITLE)
        formPanel<UploadFileForm> {
            onChangeLaunch {
                getData().filesToUpload?.sumOf { it.size }?.let { totalSize ->
                    if (totalSize > MAX_UPLOAD_SIZE) {
                        AppManager.store.dispatch(
                            UploadAction.UploadFileFailed(
                                DomainError.FileUploadError(
                                    TOAST_MAXIMUM_FILE_SIZE_EXCEEDED
                                )
                            )
                        )
                        Toast.show(TOAST_MAXIMUM_FILE_SIZE_EXCEEDED)
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
                style { display = Display.NONE }
            }
            add(UploadFileForm::filesToUpload, uploadForm)
            flexPanel(FlexDirection.COLUMN, spacing = 8, alignItems = AlignItems.CENTER) {
                flexPanel(FlexDirection.ROW, spacing = 8) {
                    button(SELECT_FILES).onClick {
                        uploadForm.input.id?.let { id -> document.getElementById(id)?.click() }
                    }
                    val uploadFileButton = button(UPLOAD_FILES) {
                        disabled = state.uploadButtonDisabled || state.isUploading
                    }
                    uploadFileButton.onClick {
                        state.kFiles?.let { files -> AppManager.uploadFiles(files) }
                    }
                }
                state.selectedFiles?.let {
                    selectedFilesList(it)
                }
            }
        }
    }
}

fun Container.selectedFilesList(selectedFilenames: List<String>) {
    divider()
    h5(SELECTED_FILES)
    flexPanel(FlexDirection.ROW, spacing = 16) {
        selectedFilenames
            .sortedBy { filename ->
                filename.filter { it.isDigit() }.toInt()
            }
            .chunked(FILE_LIST_COLUMN_SIZE)
            .forEach { filenames ->
                flexPanel(FlexDirection.COLUMN) {
                    filenames.forEach { filename ->
                        h5(filename)
                    }
                }
            }
    }
}