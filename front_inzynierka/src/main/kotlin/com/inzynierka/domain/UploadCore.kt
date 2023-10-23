package com.inzynierka.domain

import com.inzynierka.domain.service.IDataService
import io.kvision.redux.Dispatch
import io.kvision.toast.Toast
import io.kvision.types.KFile

data class UploadFilesState(
    val uploadButtonDisabled: Boolean = true,
)

sealed class UploadAction : MainAppAction() {
    object UploadFileStarted : UploadAction()
    object UploadFileSuccess : UploadAction()
    data class UploadFileFailed(val error: DomainError?) : UploadAction()
    data class UploadFormOnChangeHandler(val kFile: KFile?) : UploadAction()

}

fun uploadReducer(state: UploadFilesState, action: UploadAction) = when (action) {
    is UploadAction.UploadFileStarted -> state

    is UploadAction.UploadFileSuccess -> state

    is UploadAction.UploadFileFailed -> state

    is UploadAction.UploadFormOnChangeHandler -> state.copy(uploadButtonDisabled = action.kFile == null)
}


suspend fun uploadFiles(dispatch: Dispatch<MainAppAction>, dataService: IDataService, files: List<KFile>) {
    dispatch(UploadAction.UploadFileStarted)
    when (val result = dataService.postFiles(files)) {
        is Result.Success -> {
            Toast.info("File upload completed")
            dispatch(UploadAction.UploadFileSuccess)
        }

        is Result.Error -> {
            dispatch(UploadAction.UploadFileFailed(result.domainError))
            Toast.info("File upload failed")
        }
    }
}