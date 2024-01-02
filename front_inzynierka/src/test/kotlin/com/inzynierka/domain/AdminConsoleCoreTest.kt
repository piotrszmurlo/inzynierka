package com.inzynierka.domain

import com.inzynierka.domain.core.AdminConsoleAction
import com.inzynierka.domain.core.AdminConsoleState
import com.inzynierka.domain.core.adminConsoleReducer
import io.kvision.redux.createTypedReduxStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AdminConsoleCoreTest {
    private val initialAdminConsoleState = AdminConsoleState()
    private val store = createTypedReduxStore(::adminConsoleReducer, initialAdminConsoleState)

    @Test
    fun test_fetch_algorithms_started() {
        store.dispatch(AdminConsoleAction.FetchAlgorithmsStarted)
        assertTrue(store.getState().isFetching!!)
    }

    @Test
    fun test_fetch_algorithms_failed() {
        store.dispatch(AdminConsoleAction.FetchAlgorithmsFailed)
        assertFalse(store.getState().isFetching!!)
    }

    @Test
    fun test_fetch_algorithms_success() {
        val algorithms = listOf("Algorithm A", "Algorithm B")
        store.dispatch(AdminConsoleAction.FetchAlgorithmsSuccess(algorithms))
        assertEquals(algorithms, store.getState().algorithmNames)
        assertEquals(algorithms.firstOrNull(), store.getState().selectedAlgorithmName)
        assertFalse(store.getState().isFetching!!)
    }

    @Test
    fun test_algorithm_selected() {
        val selectedAlgorithm = "Selected Algorithm"
        store.dispatch(AdminConsoleAction.AlgorithmSelected(selectedAlgorithm))
        assertEquals(selectedAlgorithm, store.getState().selectedAlgorithmName)
    }

    @Test
    fun test_delete_algorithm_failed() {
        store.dispatch(AdminConsoleAction.DeleteAlgorithmFailed)
        assertFalse(store.getState().isDeleting!!)
    }

    @Test
    fun test_delete_algorithm_started() {
        store.dispatch(AdminConsoleAction.DeleteAlgorithmStarted)
        assertFalse(store.getState().isDeleting!!)
    }

    @Test
    fun test_delete_algorithm_success() {
        store.dispatch(AdminConsoleAction.DeleteAlgorithmSuccess)
        assertTrue(store.getState().isDeleting!!)
    }

    @Test
    fun test_promote_user_failed() {
        store.dispatch(AdminConsoleAction.PromoteUserFailed)
        assertFalse(store.getState().isFetching!!)
    }

    @Test
    fun test_promote_user_started() {
        store.dispatch(AdminConsoleAction.PromoteUserStarted)
        assertTrue(store.getState().isFetching!!)
    }

    @Test
    fun test_promote_user_success() {
        store.dispatch(AdminConsoleAction.PromoteUserSuccess)
        assertFalse(store.getState().isFetching!!)
    }
}