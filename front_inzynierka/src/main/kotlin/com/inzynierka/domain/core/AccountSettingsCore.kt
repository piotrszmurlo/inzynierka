package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.domain.models.Benchmark

data class AccountSettingsState(
    val passwordField: String? = null,
    val oldPasswordField: String? = null,
    val oldPasswordValid: Boolean = true,
    val emailField: String? = null,
    val emailValid: Boolean = true,
    val passwordValid: Boolean = true,
    val isFetching: Boolean = false,
    val handleSuccess: Boolean = false,
    val error: DomainError? = null,
    val deleteAlgorithmFormState: DeleteAlgorithmFormState = DeleteAlgorithmFormState()
)

data class DeleteAlgorithmFormState(
    val algorithmNames: List<String> = listOf(),
    val selectedAlgorithmName: String? = null,
    val benchmarkNames: List<String> = listOf(),
    val selectedBenchmarkName: String? = null,
)

sealed class AccountSettingsAction : MainAppAction() {
    data class EmailFieldChanged(val email: String?) : AccountSettingsAction()
    data class PasswordFieldChanged(val password: String?, val oldPassword: String?) : AccountSettingsAction()
    object ChangeStarted : AccountSettingsAction()
    object ChangePasswordSuccess : AccountSettingsAction()
    object ResultHandled : AccountSettingsAction()
    object ChangeEmailSuccess : AccountSettingsAction()
    data class ChangeEmailFailed(val error: DomainError) : AccountSettingsAction()

    data class ChangePasswordFailed(val error: DomainError) : AccountSettingsAction()
    object FetchAlgorithmsStarted : AccountSettingsAction()
    object FetchAlgorithmsFailed : AccountSettingsAction()
    data class FetchAlgorithmsSuccess(val algorithmNames: List<String>) : AccountSettingsAction()
    data class AlgorithmSelected(val algorithmName: String) : AccountSettingsAction()
    data class BenchmarkSelected(val benchmarkName: String) : AccountSettingsAction()
    object FetchBenchmarksFailed : AccountSettingsAction()
    data class FetchBenchmarksSuccess(val benchmark: List<Benchmark>) : AccountSettingsAction()
}

fun accountSettingsReducer(state: AccountSettingsState, action: AccountSettingsAction) = when (action) {
    is AccountSettingsAction.ChangePasswordFailed -> state.copy(error = action.error, isFetching = false)
    is AccountSettingsAction.ChangeStarted -> state.copy(isFetching = true)
    is AccountSettingsAction.ChangePasswordSuccess -> state.copy(
        isFetching = false,
        handleSuccess = true,
        oldPasswordField = null,
        passwordField = null
    )

    is AccountSettingsAction.ResultHandled -> state.copy(error = null, handleSuccess = false)
    is AccountSettingsAction.ChangeEmailFailed -> state.copy(isFetching = false, error = action.error)
    is AccountSettingsAction.ChangeEmailSuccess -> state.copy(
        isFetching = false,
        handleSuccess = true,
        emailField = null
    )

    is AccountSettingsAction.EmailFieldChanged -> state.copy(
        emailValid = isEmailValid(action.email),
        emailField = action.email
    )

    is AccountSettingsAction.PasswordFieldChanged -> state.copy(
        passwordValid = isPasswordValid(action.password),
        passwordField = action.password,
        oldPasswordField = action.oldPassword,
        oldPasswordValid = isPasswordValid(action.oldPassword)
    )

    is AccountSettingsAction.FetchAlgorithmsStarted -> state.copy(isFetching = true)
    is AccountSettingsAction.FetchAlgorithmsFailed -> state.copy(isFetching = false)
    is AccountSettingsAction.FetchAlgorithmsSuccess -> {
        state.copy(
            deleteAlgorithmFormState = state.deleteAlgorithmFormState.copy(
                algorithmNames = action.algorithmNames,
                selectedAlgorithmName = action.algorithmNames.firstOrNull(),
            ),
            isFetching = false
        )
    }

    is AccountSettingsAction.AlgorithmSelected -> state.copy(
        deleteAlgorithmFormState = state.deleteAlgorithmFormState.copy(
            selectedAlgorithmName = action.algorithmName
        )
    )

    is AccountSettingsAction.BenchmarkSelected -> state.copy(
        deleteAlgorithmFormState = state.deleteAlgorithmFormState.copy(
            selectedBenchmarkName = action.benchmarkName
        )
    )

    is AccountSettingsAction.FetchBenchmarksFailed -> state.copy(isFetching = false)
    is AccountSettingsAction.FetchBenchmarksSuccess -> {
        state.copy(
            deleteAlgorithmFormState = state.deleteAlgorithmFormState.copy(
                benchmarkNames = action.benchmark.map { it.name },
                selectedBenchmarkName = action.benchmark.firstOrNull()?.name,
            ),
            isFetching = false
        )
    }
}