package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import io.kvision.types.KFile

data class UploadFilesState(
    val isUploading: Boolean = false,
    val error: DomainError? = null,
    val kFiles: List<KFile>? = null,
    val success: Boolean? = null,
    val benchmarkNames: List<String> = listOf(),
    val selectedBenchmarkName: String? = null
) {
    val selectedFiles
        get() = kFiles?.map { it.name }
    val uploadButtonDisabled
        get() = kFiles.isNullOrEmpty() || isUploading || selectedBenchmarkName.isNullOrEmpty()
}

sealed class UploadAction : MainAppAction() {
    data class FetchAvailableBenchmarksSuccess(val benchmarkNames: List<String>) : UploadAction()
    data class FetchAvailableBenchmarksFailed(val error: DomainError) : UploadAction()
    data class BenchmarkSelected(val benchmarkName: String) : UploadAction()
    object ResultHandled : UploadAction()
    object UploadFileStarted : UploadAction()
    object UploadFileSuccess : UploadAction()
    data class UploadFileFailed(val error: DomainError?) : UploadAction()
    data class UploadFormOnChangeHandler(val kFiles: List<KFile>?) : UploadAction()

}

fun uploadReducer(state: UploadFilesState, action: UploadAction) = when (action) {
    is UploadAction.UploadFileStarted -> state.copy(isUploading = true)
    is UploadAction.UploadFileSuccess -> state.copy(isUploading = false, kFiles = null, success = true)
    is UploadAction.UploadFileFailed -> state.copy(isUploading = false, error = action.error)
    is UploadAction.UploadFormOnChangeHandler -> state.copy(kFiles = action.kFiles)
    is UploadAction.ResultHandled -> state.copy(error = null, success = null, kFiles = null)
    is UploadAction.FetchAvailableBenchmarksSuccess -> state.copy(
        benchmarkNames = action.benchmarkNames,
        selectedBenchmarkName = action.benchmarkNames.firstOrNull()
    )

    is UploadAction.FetchAvailableBenchmarksFailed -> state.copy(error = action.error)
    is UploadAction.BenchmarkSelected -> state.copy(selectedBenchmarkName = action.benchmarkName)
}