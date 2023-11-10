package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.domain.service.IDataService
import com.inzynierka.ui.show
import io.kvision.redux.Dispatch
import io.kvision.toast.Toast
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


suspend fun uploadFiles(dispatch: Dispatch<MainAppAction>, dataService: IDataService, files: List<KFile>) {
    dispatch(UploadAction.UploadFileStarted)
    when (val result = dataService.postFiles(files)) {
        is Result.Success -> {
            Toast.show("File upload completed")
            dispatch(UploadAction.UploadFileSuccess)
        }

        is Result.Error -> {
            dispatch(UploadAction.UploadFileFailed(result.domainError))
            Toast.show("File upload failed: " + (result.domainError as DomainError.FileUploadError).message)
        }
    }
}

