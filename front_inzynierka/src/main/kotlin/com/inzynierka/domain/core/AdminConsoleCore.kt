package com.inzynierka.domain.core

import com.inzynierka.common.DomainError

data class AdminConsoleState(
    val algorithmNames: List<String> = listOf(),
    val userEmail: String? = null,
    val selectedAlgorithmName: String? = null,
    val error: DomainError? = null,
    val isDeleting: Boolean? = null,
    val isFetching: Boolean? = null
) {
    val deleteButtonDisabled = algorithmNames.isEmpty()
}

sealed class AdminConsoleAction : MainAppAction() {
    object FetchAlgorithmsStarted : AdminConsoleAction()
    object FetchAlgorithmsFailed : AdminConsoleAction()
    data class FetchAlgorithmsSuccess(val algorithmNames: List<String>) : AdminConsoleAction()
    object PromoteUserStarted : AdminConsoleAction()
    object PromoteUserSuccess : AdminConsoleAction()
    object PromoteUserFailed : AdminConsoleAction()
    data class AlgorithmSelected(val algorithmName: String) : AdminConsoleAction()
    object DeleteAlgorithmStarted : AdminConsoleAction()
    object DeleteAlgorithmFailed : AdminConsoleAction()
    object DeleteAlgorithmSuccess : AdminConsoleAction()


}

fun adminConsoleReducer(state: AdminConsoleState, action: AdminConsoleAction) = when (action) {
    is AdminConsoleAction.FetchAlgorithmsStarted -> state.copy(isFetching = true)
    is AdminConsoleAction.FetchAlgorithmsFailed -> state.copy(isFetching = false)
    is AdminConsoleAction.FetchAlgorithmsSuccess -> {
        state.copy(
            algorithmNames = action.algorithmNames,
            selectedAlgorithmName = action.algorithmNames.firstOrNull(),
            isFetching = false
        )
    }

    is AdminConsoleAction.AlgorithmSelected -> state.copy(selectedAlgorithmName = action.algorithmName)
    is AdminConsoleAction.DeleteAlgorithmFailed -> state.copy(isDeleting = false)
    is AdminConsoleAction.DeleteAlgorithmStarted -> state.copy(isDeleting = false)
    is AdminConsoleAction.DeleteAlgorithmSuccess -> state.copy(isDeleting = true)
    is AdminConsoleAction.PromoteUserFailed -> state.copy(isFetching = false)
    is AdminConsoleAction.PromoteUserStarted -> state.copy(isFetching = true)
    is AdminConsoleAction.PromoteUserSuccess -> state.copy(isFetching = false)
}