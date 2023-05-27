package com.inzynierka.domain

import com.inzynierka.data.DomainError
import io.kvision.redux.RAction
import io.kvision.types.KFile
import org.koin.core.component.KoinComponent

data class MainAppState(
    val data: List<Int>,
    val isFetching: Boolean,
    val success: Boolean,
    val error: DomainError?,
    val uploadButtonDisabled: Boolean = true
) : KoinComponent

sealed class MainAppAction : RAction {
    object FetchDataStarted : MainAppAction()
    object UploadFileStarted : MainAppAction()
    object UploadFileSuccess : MainAppAction()
    object ErrorHandled : MainAppAction()
    data class UploadFormOnChangeHandler(val kFile: KFile?) : MainAppAction()
    data class UploadFileFailed(val error: DomainError) : MainAppAction()
    data class FetchDataSuccess(val data: List<Int>) : MainAppAction()
    data class FetchDataFailed(val error: DomainError) : MainAppAction()
}

fun mainAppReducer(state: MainAppState, action: MainAppAction): MainAppState = when (action) {
    is MainAppAction.FetchDataStarted -> {
        state.copy(isFetching = true)
    }

    is MainAppAction.FetchDataFailed -> {
        state.copy(error = action.error)
    }


    is MainAppAction.FetchDataSuccess -> {
        state.copy(
            data = action.data,
            success = true
        )
    }

    is MainAppAction.UploadFileStarted -> {
        state
    }

    is MainAppAction.UploadFileFailed -> {
        state
    }

    is MainAppAction.UploadFileSuccess -> {
        state
    }

    is MainAppAction.ErrorHandled -> {
        state.copy(error = null)
    }

    is MainAppAction.UploadFormOnChangeHandler -> {
        state.copy(uploadButtonDisabled = action.kFile == null)
    }
}
