package com.inzynierka.domain.core

import com.inzynierka.common.DomainError

data class AccountSettingsState(
    val passwordField: String? = null,
    val oldPasswordField: String? = null,
    val oldPasswordValid: Boolean? = null,
    val emailField: String? = null,
    val emailValid: Boolean = true,
    val passwordValid: Boolean = true,
    val isFetching: Boolean = false,
    val handleSuccess: Boolean = false,
    val error: DomainError? = null,
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
}

fun accountSettingsReducer(state: AccountSettingsState, action: AccountSettingsAction) = when (action) {
    is AccountSettingsAction.ChangePasswordFailed -> state.copy(error = action.error, isFetching = false)
    is AccountSettingsAction.ChangeStarted -> state.copy(isFetching = true)
    is AccountSettingsAction.ChangePasswordSuccess -> state.copy(isFetching = false, handleSuccess = true)
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
        oldPasswordValid = isPasswordValid(action.oldPassword)
    )
}