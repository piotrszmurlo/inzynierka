package com.inzynierka.domain.core


import com.inzynierka.common.DomainError

private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

data class UserData(
    val disabled: Boolean,
    val isUserAdmin: Boolean
)

data class LoginState(
    val email: String? = "",
    val emailValid: Boolean = true,
    val passwordValid: Boolean = true,
    val password: String? = "",
    val error: DomainError? = null,
    val isUserLoggedIn: Boolean = false,
    val isRegisteringOrLoggingIn: Boolean = false,
    val loggedInUserData: UserData? = null,
)

sealed class LoginAction : MainAppAction() {
    object Login : LoginAction()
    object Logout : LoginAction()
    data class LoginSuccess(val loggedInUserData: UserData?) : LoginAction()
    object Register : LoginAction()
    data class RegisterSuccess(val data: UserData?) : LoginAction()
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
            loggedInUserData = action.loggedInUserData
        )
    }

    is LoginAction.Register -> state.copy(isRegisteringOrLoggingIn = true)
    is LoginAction.RegisterFailed -> state.copy(isRegisteringOrLoggingIn = false)
    is LoginAction.RegisterSuccess -> state.copy(
        email = "",
        password = "",
        isUserLoggedIn = true,
        isRegisteringOrLoggingIn = false,
        loggedInUserData = action.data
    )

    is LoginAction.Logout -> state.copy(isUserLoggedIn = false, loggedInUserData = null)
}


fun isEmailValid(email: String?): Boolean {
    return email?.matches(EMAIL_REGEX.toRegex()) ?: false
}

fun isPasswordValid(password: String?): Boolean {
    return password?.let {
        it.length >= 8 && it.all { char -> !char.isWhitespace() } && it.isNotBlank()
    } ?: false
}