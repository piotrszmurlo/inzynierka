package com.inzynierka.domain.core

import com.inzynierka.common.DomainError

data class AdminConsoleState(
    val algorithmNames: List<String> = listOf(),
    val selectedAlgorithmName: String? = null,
    val error: DomainError? = null,
    val isDeleting: Boolean? = null
) {
    val deleteButtonDisabled = algorithmNames.isEmpty()
}

sealed class AdminConsoleAction : MainAppAction() {
    object FetchAlgorithmsStarted : AdminConsoleAction()
    object FetchAlgorithmsFailed : AdminConsoleAction()
    data class FetchAlgorithmsSuccess(val algorithmNames: List<String>) : AdminConsoleAction()
    data class AlgorithmSelected(val algorithmName: String) : AdminConsoleAction()
    object DeleteAlgorithmStarted : AdminConsoleAction()
    object DeleteAlgorithmFailed : AdminConsoleAction()
    object DeleteAlgorithmSuccess : AdminConsoleAction()


}

fun adminConsoleReducer(state: AdminConsoleState, action: AdminConsoleAction) = when (action) {
    is AdminConsoleAction.FetchAlgorithmsStarted -> state
    is AdminConsoleAction.FetchAlgorithmsFailed -> state
    is AdminConsoleAction.FetchAlgorithmsSuccess -> {
        state.copy(
            algorithmNames = action.algorithmNames,
            selectedAlgorithmName = action.algorithmNames.firstOrNull()
        )
    }

    is AdminConsoleAction.AlgorithmSelected -> state.copy(selectedAlgorithmName = action.algorithmName)
    is AdminConsoleAction.DeleteAlgorithmFailed -> state.copy(isDeleting = false)
    is AdminConsoleAction.DeleteAlgorithmStarted -> state.copy(isDeleting = false)
    is AdminConsoleAction.DeleteAlgorithmSuccess -> state.copy(isDeleting = true)
}