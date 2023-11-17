package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import io.kvision.types.KFile

data class UploadFilesState(
    val isUploading: Boolean = false,
    val error: DomainError? = null,
    val kFiles: List<KFile>? = null,
    val success: Boolean? = null
) {
    val selectedFiles
        get() = kFiles?.map { it.name }
    val uploadButtonDisabled
        get() = kFiles.isNullOrEmpty() || isUploading
}

sealed class UploadAction : MainAppAction() {
    object ResultHandled : UploadAction()
    object UploadFileStarted : UploadAction()
    object UploadFileSuccess : UploadAction()
    data class UploadFileFailed(val error: DomainError?) : UploadAction()
    data class UploadFormOnChangeHandler(val kFiles: List<KFile>?) : UploadAction()

}

fun uploadReducer(state: UploadFilesState, action: UploadAction) = when (action) {
    is UploadAction.UploadFileStarted -> state.copy(isUploading = true)
    is UploadAction.UploadFileSuccess -> state.copy(isUploading = false, kFiles = null, success = true)
    is UploadAction.UploadFileFailed -> state.copy(isUploading = false, error = action.error, kFiles = null)
    is UploadAction.UploadFormOnChangeHandler -> state.copy(kFiles = action.kFiles)
    is UploadAction.ResultHandled -> state.copy(error = null, success = null)
}