package com.inzynierka.domain

import com.inzynierka.data.DomainError
import io.kvision.redux.RAction
import io.kvision.types.KFile
import org.koin.core.component.KoinComponent

data class MainAppState(
    val data: List<Int>,
    val isFetching: Boolean,
    val success: Boolean,
    val error: DomainError?
) : KoinComponent

sealed class MainAppAction : RAction {
    object FetchDataStarted : MainAppAction()
    data class FetchDataSuccess(val data: List<Int>) : MainAppAction()
    data class FetchDataFailed(val error: DomainError) : MainAppAction()
    data class UploadFile(val file: List<KFile>?) : MainAppAction()
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

    is MainAppAction.UploadFile -> {
        console.log(action.file?.get(0)?.content)
        state
    }
}
