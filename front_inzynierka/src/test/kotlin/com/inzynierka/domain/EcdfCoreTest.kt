package com.inzynierka.domain

import com.inzynierka.common.DomainError
import com.inzynierka.domain.core.EcdfAction
import com.inzynierka.domain.core.EcdfState
import com.inzynierka.domain.core.ecdfReducer
import com.inzynierka.domain.models.RankingType
import com.inzynierka.model.EcdfData
import io.kvision.redux.createTypedReduxStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class EcdfCoreTest {
    private val initialEcdfState = EcdfState()
    private val store = createTypedReduxStore(::ecdfReducer, initialEcdfState)

    @Test
    fun test_fetch_rankings_success() {
        val ecdfDataList = listOf(
            EcdfData(1, 10, "Algorithm A", listOf(0.1, 0.2, 0.3), listOf(10.0, 20.0, 30.0)),
            EcdfData(1, 10, "Algorithm B", listOf(0.2, 0.3, 0.4), listOf(20.0, 30.0, 40.0))
        )
        store.dispatch(EcdfAction.FetchRankingsSuccess(ecdfDataList))

        val state = store.getState()
        assertTrue(state.splitData != null)
        assertTrue(state.combinedData != null)
        assertTrue(state.functionGroupData != null)
        assertFalse(state.isFetching)
        assertEquals(RankingType.PerFunction, state.rankingType)
    }

    @Test
    fun test_fetch_rankings_failed() {
        val error = DomainError("Failed to fetch ECDF rankings")
        store.dispatch(EcdfAction.FetchRankingsFailed(error))

        val state = store.getState()
        assertFalse(state.isFetching)
    }

    @Test
    fun test_fetch_rankings_started() {
        store.dispatch(EcdfAction.FetchRankingsStarted)

        val state = store.getState()
        assertTrue(state.isFetching)
        assertTrue(state.splitData == null)
        assertTrue(state.combinedData == null)
        assertTrue(state.functionGroupData == null)
    }

    @Test
    fun test_ecdf_type_changed() {
        val newType = RankingType.Averaged // Define the desired ranking type
        store.dispatch(EcdfAction.EcdfTypeChanged(newType))

        val state = store.getState()
        assertEquals(newType, state.rankingType)
    }
}
