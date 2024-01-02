package com.inzynierka.domain

import com.inzynierka.domain.core.*
import io.kvision.redux.createTypedReduxStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MainAppCoreTest {

    private val initialState = MainAppState(tab = Tab.Upload)
    private val store = createTypedReduxStore(::mainAppReducer, initialState)

    @Test
    fun test_state_logic() {
        val state = MainAppState(
            tab = Tab.Upload,
            loginState = LoginState(
                isUserLoggedIn = true,
                loggedInUserData = UserData(disabled = true, isUserAdmin = false)
            ),
        )
        assertTrue(state.isUserLoggedIn)
        assertFalse(state.isUserVerified)

        val state2 = MainAppState(
            tab = Tab.Upload,
            loginState = LoginState(
                isUserLoggedIn = true,
                loggedInUserData = UserData(disabled = false, isUserAdmin = false)
            ),
        )
        assertTrue(state2.isUserLoggedIn)
        assertTrue(state2.isUserVerified)

        val state3 = MainAppState(
            tab = Tab.Upload,
            loginState = LoginState(
                isUserLoggedIn = false,
                loggedInUserData = null
            ),
        )
        assertFalse(state3.isUserLoggedIn)
        assertFalse(state3.isUserVerified)
    }

    @Test
    fun test_tab_selected_action() {
        store.dispatch(MainAppAction.TabSelected(Tab.AdminConsole))
        assertEquals(Tab.AdminConsole, store.getState().tab)

        store.dispatch(MainAppAction.TabSelected(Tab.ResultsTab.PairTest))
        assertEquals(Tab.ResultsTab.PairTest, store.getState().tab)
    }

}