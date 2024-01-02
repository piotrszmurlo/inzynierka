package com.inzynierka.domain

import com.inzynierka.common.DomainError
import com.inzynierka.domain.core.FriedmanRankingAction
import com.inzynierka.domain.core.FriedmanRankingState
import com.inzynierka.domain.core.friedmanReducer
import com.inzynierka.domain.models.ScoreRankingEntry
import io.kvision.redux.createTypedReduxStore
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class FriedmanRankingCoreTest {
    private val initialFriedmanState = FriedmanRankingState()
    private val store = createTypedReduxStore(::friedmanReducer, initialFriedmanState)

    @Test
    fun test_fetch_rankings_success() {
        val scoreRankingEntries = listOf(
            ScoreRankingEntry(null, 20, "IMPML-SHADE", 1, 3.0),
            ScoreRankingEntry(null, 20, "NL-SHADE-RSP-MID", 1, 4.0),
            ScoreRankingEntry(null, 20, "S_LSHADE_DP", 1, 2.0),
            ScoreRankingEntry(null, 20, "IUMOEAII", 1, 1.0),
            ScoreRankingEntry(null, 20, "IUMOEAII", 2, 4.0),
            ScoreRankingEntry(null, 20, "IMPML-SHADE", 2, 2.0),
            ScoreRankingEntry(null, 20, "NL-SHADE-RSP-MID", 2, 3.0),
            ScoreRankingEntry(null, 20, "S_LSHADE_DP", 2, 1.0),
            ScoreRankingEntry(null, 20, "IMPML-SHADE", 3, 3.0),
            ScoreRankingEntry(null, 20, "IUMOEAII", 3, 2.0),
            ScoreRankingEntry(null, 20, "NL-SHADE-RSP-MID", 3, 4.0),
            ScoreRankingEntry(null, 20, "S_LSHADE_DP", 3, 1.0)
        )
        store.dispatch(FriedmanRankingAction.FetchRankingsSuccess(scoreRankingEntries))
        val expected = listOf(
            ScoreRankingEntry(
                rank = 1,
                dimension = 20,
                algorithmName = "S_LSHADE_DP",
                functionNumber = 1,
                score = 1.3333333333333333
            ),
            ScoreRankingEntry(
                rank = 2,
                dimension = 20,
                algorithmName = "IUMOEAII",
                functionNumber = 1,
                score = 2.3333333333333335
            ),
            ScoreRankingEntry(
                rank = 3,
                dimension = 20,
                algorithmName = "IMPML-SHADE",
                functionNumber = 1,
                score = 2.6666666666666665
            ),
            ScoreRankingEntry(
                rank = 4,
                dimension = 20,
                algorithmName = "NL-SHADE-RSP-MID",
                functionNumber = 1,
                score = 3.6666666666666665
            )
        )
        val state = store.getState()
        assertEquals(
            expected,
            state.combinedScores
        )
        assertTrue(state.combinedScores != null)
        assertFalse(state.isFetching)
    }

    @Test
    fun test_fetch_rankings_failed() {
        val error = DomainError("test error")
        store.dispatch(FriedmanRankingAction.FetchRankingsFailed(error))

        val state = store.getState()
        assertFalse(state.isFetching)
    }

    @Test
    fun test_fetch_rankings_started() {
        store.dispatch(FriedmanRankingAction.FetchRankingsStarted)

        val state = store.getState()
        assertTrue(state.isFetching)
    }
}
