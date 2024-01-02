package com.inzynierka.domain

import com.inzynierka.common.DomainError
import com.inzynierka.domain.core.LoginAction
import com.inzynierka.domain.core.LoginState
import com.inzynierka.domain.core.UserData
import com.inzynierka.domain.core.loginReducer
import io.kvision.redux.createTypedReduxStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LoginCoreTest {
    private val initialLoginState = LoginState()
    private val store = createTypedReduxStore(::loginReducer, initialLoginState)

    @Test
    fun test_correct_email() {
        store.dispatch(LoginAction.EmailChanged("correct@test.com"))
        assertEquals(
            "correct@test.com",
            store.getState().email
        )
        assertTrue(store.getState().emailValid)
    }

    @Test
    fun test_incorrect_email() {
        store.dispatch(LoginAction.EmailChanged("incorrect"))
        assertEquals(
            "incorrect",
            store.getState().email
        )
        assertFalse(store.getState().emailValid)

    }

    @Test
    fun test_correct_password() {
        store.dispatch(LoginAction.EmailChanged("correct@test.com"))
        assertEquals(
            "correct@test.com",
            store.getState().email
        )
        assertTrue(store.getState().emailValid)
    }

    @Test
    fun test_incorrect_password() {
        store.dispatch(LoginAction.PasswordChanged("bad"))
        assertEquals(
            "bad",
            store.getState().password
        )
        assertFalse(store.getState().passwordValid)
    }

    @Test
    fun test_login_success() {
        val fakeUserData = UserData(disabled = true, isUserAdmin = false)
        store.dispatch(LoginAction.LoginSuccess(fakeUserData))
        assertEquals(
            initialLoginState.copy(
                email = "",
                password = "",
                isRegisteringOrLoggingIn = false,
                isUserLoggedIn = true,
                loggedInUserData = fakeUserData
            ),
            store.getState()
        )
    }

    @Test
    fun test_register_success() {
        val fakeUserData = UserData(disabled = true, isUserAdmin = false)
        store.dispatch(LoginAction.RegisterSuccess(fakeUserData))
        assertEquals(
            initialLoginState.copy(
                email = "",
                password = "",
                isRegisteringOrLoggingIn = false,
                isUserLoggedIn = true,
                loggedInUserData = fakeUserData
            ),
            store.getState()
        )
    }

    @Test
    fun test_login_clicked() {
        store.dispatch(LoginAction.Login)
        assertTrue(
            store.getState().isRegisteringOrLoggingIn
        )
    }

    @Test
    fun test_login_failed() {
        store.dispatch(LoginAction.LoginFailed(DomainError("fake error")))
        assertFalse(
            store.getState().isRegisteringOrLoggingIn
        )
    }

    @Test
    fun test_register_clicked() {
        store.dispatch(LoginAction.Register)
        assertTrue(
            store.getState().isRegisteringOrLoggingIn
        )
    }

    @Test
    fun test_register_failed() {
        store.dispatch(LoginAction.RegisterFailed(DomainError("fake error")))
        assertFalse(
            store.getState().isRegisteringOrLoggingIn
        )
    }

    @Test
    fun test_empty_email() {
        store.dispatch(LoginAction.EmailChanged(""))
        assertEquals(
            "",
            store.getState().email
        )
        assertFalse(store.getState().emailValid)
    }

    @Test
    fun test_empty_password() {
        store.dispatch(LoginAction.PasswordChanged(""))
        assertEquals(
            "",
            store.getState().password
        )
        assertFalse(store.getState().passwordValid)
    }

    @Test
    fun test_login_success_with_null_user_data() {
        store.dispatch(LoginAction.LoginSuccess(null))
        assertEquals(
            initialLoginState.copy(
                email = "",
                password = "",
                isRegisteringOrLoggingIn = false,
                isUserLoggedIn = true,
                loggedInUserData = null
            ),
            store.getState()
        )
    }

    @Test
    fun test_register_success_with_null_user_data() {
        store.dispatch(LoginAction.RegisterSuccess(null))
        assertEquals(
            initialLoginState.copy(
                email = "",
                password = "",
                isRegisteringOrLoggingIn = false,
                isUserLoggedIn = true,
                loggedInUserData = null
            ),
            store.getState()
        )
    }
}