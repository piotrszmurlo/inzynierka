package com.inzynierka.domain

import com.inzynierka.common.DomainError
import com.inzynierka.domain.core.Cec2022RankingAction
import com.inzynierka.domain.core.ScoreRankingState
import com.inzynierka.domain.core.cec2022Reducer
import com.inzynierka.domain.models.ScoreRankingEntry
import io.kvision.redux.createTypedReduxStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Cec2022RankingCoreTest {
    private val initialScoreRankingState = ScoreRankingState()
    private val store = createTypedReduxStore(::cec2022Reducer, initialScoreRankingState)

    @Test
    fun test_fetch_rankings_success() {
        val scores = listOf(
            ScoreRankingEntry(1, 10, "Algorithm A", 1, 100.0),
            ScoreRankingEntry(2, 10, "Algorithm B", 2, 90.0)
        )
        store.dispatch(Cec2022RankingAction.FetchRankingsSuccess(scores))

        val state = store.getState()
        assertTrue(state.scores != null)
        assertEquals(scores, state.combinedScores)
        assertFalse(state.isFetching)
    }

    @Test
    fun test_fetch_rankings_failed() {
        val error = DomainError("Failed to fetch rankings")
        store.dispatch(Cec2022RankingAction.FetchRankingsFailed(error))
        val state = store.getState()
        assertFalse(state.isFetching)
    }

    @Test
    fun test_fetch_rankings_started() {
        store.dispatch(Cec2022RankingAction.FetchRankingsStarted)
        val state = store.getState()
        assertTrue(state.isFetching)
        assertTrue(state.scores == null)
        assertTrue(state.combinedScores == null)
    }
}
