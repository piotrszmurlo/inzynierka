package com.inzynierka.ui

import com.inzynierka.domain.core.UploadAction
import com.inzynierka.domain.core.UploadFilesState
import com.inzynierka.ui.StringResources.BENCHMARK_DESCRIPTION
import com.inzynierka.ui.StringResources.FUNCTION_COUNT_DESCRIPTION
import com.inzynierka.ui.StringResources.OVERWRITE_FILES_DIALOG_TEXT
import com.inzynierka.ui.StringResources.SELECTED_FILES
import com.inzynierka.ui.StringResources.SELECT_BENCHMARK
import com.inzynierka.ui.StringResources.SELECT_FILES
import com.inzynierka.ui.StringResources.TOAST_FILE_UPLOAD_COMPLETED
import com.inzynierka.ui.StringResources.TOAST_MAXIMUM_FILE_SIZE_EXCEEDED
import com.inzynierka.ui.StringResources.TRIAL_COUNT_DESCRIPTION
import com.inzynierka.ui.StringResources.UPLOAD_FILES
import com.inzynierka.ui.StringResources.UPLOAD_FILE_TAB_TITLE
import io.kvision.core.*
import io.kvision.form.formPanel
import io.kvision.form.getDataWithFileContent
import io.kvision.form.select.select
import io.kvision.form.upload.upload
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.modal.Confirm
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
private const val UPLOAD_FORM_ID = "UploadFormId"

@Serializable
private data class UploadFileForm(
    val filesToUpload: List<KFile>? = null,
    val benchmarkName: String? = null
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
                input.id = UPLOAD_FORM_ID
                style { display = Display.NONE }
            }
            add(UploadFileForm::filesToUpload, uploadForm)
            flexPanel(FlexDirection.COLUMN, spacing = 16, alignItems = AlignItems.CENTER) {
                h5(SELECT_BENCHMARK)
                val benchmarkSelect = select(
                    options = state.benchmarks.map { it.name to it.name },
                    value = state.selectedBenchmarkName
                ) {
                    width = 250.px
                }
                benchmarkSelect.bind(UploadFileForm::benchmarkName)
                benchmarkSelect.onChange {
                    AppManager.store.dispatch(
                        UploadAction.BenchmarkSelected(
                            benchmarkName = this.value!!,
                        )
                    )
                }
                h5(state.currentBenchmark?.let { BENCHMARK_DESCRIPTION(it.description) })
                h5(state.currentBenchmark?.let { TRIAL_COUNT_DESCRIPTION(it.trialCount) })
                h5(state.currentBenchmark?.let { FUNCTION_COUNT_DESCRIPTION(it.functionCount) })
                flexPanel(FlexDirection.ROW, spacing = 8) {
                    button(SELECT_FILES).onClick {
                        uploadForm.input.id?.let { id -> document.getElementById(id)?.click() }
                    }
                    button(UPLOAD_FILES) {
                        disabled = state.uploadButtonDisabled
                    }.onClick {
                        state.kFiles?.let { files ->
                            state.selectedBenchmarkName?.let { benchmark ->
                                AppManager.uploadFiles(
                                    files = files, benchmarkName = benchmark, overwriteExisting = false
                                )
                            }
                        }
                    }

                }
                state.selectedFiles?.let {
                    selectedFilesList(it)
                }
            }
        }
    }
    state.error?.let {
        if (it.message?.contains("File already exists") == true) {
            Confirm.show(text = OVERWRITE_FILES_DIALOG_TEXT) {
                state.kFiles?.let { files ->
                    state.selectedBenchmarkName?.let { benchmark ->
                        AppManager.uploadFiles(
                            files = files,
                            benchmarkName = benchmark,
                            overwriteExisting = true
                        )
                    }
                }
            }
        } else {
            Toast.show(StringResources.FILE_UPLOAD_ERROR(it.message))
        }
        AppManager.store.dispatch(UploadAction.ResultHandled)
    }
    state.success?.let {
        Toast.show(TOAST_FILE_UPLOAD_COMPLETED)
        AppManager.store.dispatch(UploadAction.ResultHandled)
    }
}

fun Container.selectedFilesList(selectedFilenames: List<String>) {
    divider()
    h5(SELECTED_FILES)
    flexPanel(FlexDirection.ROW, spacing = 16) {
        selectedFilenames
            .sortedBy { filename ->
                filename.filter { it.isDigit() }.toIntOrNull()
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