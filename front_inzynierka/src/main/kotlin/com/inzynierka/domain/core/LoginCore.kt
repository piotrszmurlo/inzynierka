package com.inzynierka.domain.core


import com.inzynierka.common.DomainError

private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

data class LoginState(
    val email: String? = "",
    val emailValid: Boolean = true,
    val passwordValid: Boolean = true,
    val password: String? = "",
    val error: DomainError? = null,
    val isUserLoggedIn: Boolean = false,
    val isUserAdmin: Boolean = false,
    val isRegisteringOrLoggingIn: Boolean = false
)

sealed class LoginAction : MainAppAction() {
    object Login : LoginAction()
    data class LoginSuccess(val isUserAdmin: Boolean) : LoginAction()
    object Register : LoginAction()
    object RegisterSuccess : LoginAction()
    data class EmailChanged(val email: String?) : LoginAction()
    data class PasswordChanged(val password: String?) : LoginAction()
    data class LoginFailed(val domainError: DomainError) : LoginAction()
    data class RegisterFailed(val domainError: DomainError) : LoginAction()
}

fun loginReducer(state: LoginState, action: LoginAction) = when (action) {
    is LoginAction.EmailChanged -> {
        state.copy(
            email = action.email,
            emailValid = isEmailValid(action.email)
        )
    }

    is LoginAction.PasswordChanged -> {
        state.copy(
            password = action.password,
            passwordValid = isPasswordValid(action.password)
        )
    }

    is LoginAction.Login -> state.copy(isRegisteringOrLoggingIn = true)
    is LoginAction.LoginFailed -> state.copy(isRegisteringOrLoggingIn = false)
    is LoginAction.LoginSuccess -> {
        state.copy(
            email = "",
            password = "",
            isRegisteringOrLoggingIn = false,
            isUserLoggedIn = true,
            isUserAdmin = action.isUserAdmin
        )
    }

    is LoginAction.Register -> state.copy(isRegisteringOrLoggingIn = true)
    is LoginAction.RegisterFailed -> state.copy(isRegisteringOrLoggingIn = false)
    is LoginAction.RegisterSuccess -> state.copy(email = "", password = "", isUserLoggedIn = true)
}


fun isEmailValid(email: String?): Boolean {
    return email?.matches(EMAIL_REGEX.toRegex()) ?: false
}

fun isPasswordValid(password: String?): Boolean {
    return password?.let {
        it.length >= 8 && it.all { char -> !char.isWhitespace() } && it.isNotBlank()
    } ?: false
}