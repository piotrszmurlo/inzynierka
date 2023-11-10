package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import io.kvision.types.KFile

data class UploadFilesState(
    val uploadButtonDisabled: Boolean = true,
    val isUploading: Boolean = false,
    val error: DomainError? = null
)

sealed class UploadAction : MainAppAction() {
    object UploadFileStarted : UploadAction()
    object UploadFileSuccess : UploadAction()
    data class UploadFileFailed(val error: DomainError?) : UploadAction()
    data class UploadFormOnChangeHandler(val kFile: KFile?) : UploadAction()

}

fun uploadReducer(state: UploadFilesState, action: UploadAction) = when (action) {
    is UploadAction.UploadFileStarted -> state.copy(isUploading = true)
    is UploadAction.UploadFileSuccess -> state.copy(isUploading = false)
    is UploadAction.UploadFileFailed -> state.copy(isUploading = false, error = action.error)
    is UploadAction.UploadFormOnChangeHandler -> state.copy(uploadButtonDisabled = action.kFile == null)
}