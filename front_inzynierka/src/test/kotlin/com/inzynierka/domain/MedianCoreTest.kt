package com.inzynierka.domain

import com.inzynierka.common.DomainError
import com.inzynierka.domain.core.MedianRankingAction
import com.inzynierka.domain.core.NumberNotation
import com.inzynierka.domain.core.StatisticsRankingState
import com.inzynierka.domain.core.medianReducer
import com.inzynierka.domain.models.StatisticsRankingEntry
import io.kvision.redux.createTypedReduxStore
import kotlin.test.*

class MedianCoreTest {
    private val initialMedianState = StatisticsRankingState()
    private val store = createTypedReduxStore(::medianReducer, initialMedianState)

    @Test
    fun test_fetch_rankings_started() {
        store.dispatch(MedianRankingAction.FetchRankingsStarted)
        assertTrue(store.getState().isFetching)
    }

    @Test
    fun test_fetch_rankings_success() {
        store.dispatch(MedianRankingAction.FetchRankingsSuccess(statisticsRankingEntries))
        assertFalse(store.getState().isFetching)
        assertEquals(
            setOf(10, 20),
            store.getState().scores!!.keys
        )
        assertEquals(
            setOf(1, 2),
            store.getState().scores!![10]!!.keys
        )
        assertEquals(
            setOf(1, 2),
            store.getState().scores!![20]!!.keys
        )
        assertEquals(
            store.getState().scores!![10]!![1],
            listOf(
                StatisticsRankingEntry(
                    rank = 1,
                    dimension = 10,
                    algorithmName = "NL-SHADE-RSP-MID",
                    functionNumber = 1,
                    mean = 1.0000000000000005e-8,
                    median = 1e-8,
                    stdev = 1e-8,
                    max = 1e-8,
                    min = 1e-8,
                    minEvaluations = 56612
                ),
                StatisticsRankingEntry(
                    rank = 2,
                    dimension = 10,
                    algorithmName = "IUMOEAII",
                    functionNumber = 1,
                    mean = 1.0000000000000005e-8,
                    median = 1e-8,
                    stdev = 1e-8,
                    max = 1e-8,
                    min = 1e-8,
                    minEvaluations = 63769
                ),
                StatisticsRankingEntry(
                    rank = 3,
                    dimension = 10,
                    algorithmName = "IMPML-SHADE",
                    functionNumber = 1,
                    mean = 1.0000000000000005e-8,
                    median = 1e-8,
                    stdev = 1e-8,
                    max = 1e-8,
                    min = 1e-8,
                    minEvaluations = 69810
                ),
                StatisticsRankingEntry(
                    rank = 4,
                    dimension = 10,
                    algorithmName = "S_LSHADE_DP",
                    functionNumber = 1,
                    mean = 1.0000000000000005e-8,
                    median = 1e-8,
                    stdev = 1e-8,
                    max = 1e-8,
                    min = 1e-8,
                    minEvaluations = 123978
                )
            )
        )
    }

    @Test
    fun test_fetch_rankings_failed() {
        val error = DomainError("Fake error message")
        store.dispatch(MedianRankingAction.FetchRankingsFailed(error))
        assertFalse(store.getState().isFetching)
        assertEquals(error, store.getState().error)
    }

    @Test
    fun test_toggle_number_notation() {
        store.dispatch(MedianRankingAction.ToggleNumberNotation)
        assertEquals(NumberNotation.Decimal, store.getState().numberNotation)

        store.dispatch(MedianRankingAction.ToggleNumberNotation)
        assertEquals(NumberNotation.Scientific, store.getState().numberNotation)
    }

    @Test
    fun test_change_precision() {
        val newPrecision = 4
        store.dispatch(MedianRankingAction.ChangePrecision(newPrecision))
        assertEquals(newPrecision, store.getState().numberPrecision)
    }

    @Test
    fun test_error_handled() {
        store.dispatch(MedianRankingAction.ErrorHandled)
        assertNull(store.getState().error)
    }

    companion object {
        private val statisticsRankingEntries = listOf(
            StatisticsRankingEntry(null, 20, "S_LSHADE_DP", 1, 1.0000000000000005e-8, 1e-8, 1e-8, 1e-8, 1e-8, 561039),
            StatisticsRankingEntry(null, 20, "IUMOEAII", 1, 1.0000000000000005e-8, 1e-8, 1e-8, 1e-8, 1e-8, 328516),
            StatisticsRankingEntry(
                null,
                20,
                "IMPML-SHADE",
                1,
                4.111999999999997e-8,
                1e-8,
                6.590460479612432e-8,
                1e-8,
                1e-8,
                641221
            ),
            StatisticsRankingEntry(
                null,
                20,
                "NL-SHADE-RSP-MID",
                1,
                1.0000000000000005e-8,
                1e-8,
                1e-8,
                1e-8,
                1e-8,
                295937
            ),
            StatisticsRankingEntry(null, 10, "S_LSHADE_DP", 1, 1.0000000000000005e-8, 1e-8, 1e-8, 1e-8, 1e-8, 123978),
            StatisticsRankingEntry(null, 10, "IUMOEAII", 1, 1.0000000000000005e-8, 1e-8, 1e-8, 1e-8, 1e-8, 63769),
            StatisticsRankingEntry(null, 10, "IMPML-SHADE", 1, 1.0000000000000005e-8, 1e-8, 1e-8, 1e-8, 1e-8, 69810),
            StatisticsRankingEntry(
                null,
                10,
                "NL-SHADE-RSP-MID",
                1,
                1.0000000000000005e-8,
                1e-8,
                1e-8,
                1e-8,
                1e-8,
                56612
            ),
            StatisticsRankingEntry(
                null,
                20,
                "S_LSHADE_DP",
                2,
                0.4030260090000001,
                1e-8,
                1.209077997,
                4.03026,
                1e-8,
                577444
            ),
            StatisticsRankingEntry(
                null,
                20,
                "IUMOEAII",
                2,
                40.44008514100002,
                44.895469,
                15.595661133398206,
                49.084479,
                1e-8,
                850828
            ),
            StatisticsRankingEntry(
                null,
                20,
                "IMPML-SHADE",
                2,
                2.5528526856666662,
                2.8605473999999997,
                1.4886912478693752,
                5.3442111,
                0.19959075,
                1000000
            ),
            StatisticsRankingEntry(
                null,
                20,
                "NL-SHADE-RSP-MID",
                2,
                8.928774202326668,
                0.3500645,
                16.415276230336943,
                49.0845,
                0.0000364598,
                1000000
            ),
            StatisticsRankingEntry(null, 10, "S_LSHADE_DP", 2, 1.0000000000000005e-8, 1e-8, 1e-8, 1e-8, 1e-8, 128319),
            StatisticsRankingEntry(null, 10, "IUMOEAII", 2, 1.0000000000000005e-8, 1e-8, 1e-8, 1e-8, 1e-8, 93797),
            StatisticsRankingEntry(
                null,
                10,
                "IMPML-SHADE",
                2,
                0.0012301326666666668,
                0.0002023925,
                0.0017732661948301,
                0.007123566,
                1e-8,
                91300
            ),
            StatisticsRankingEntry(
                null,
                10,
                "NL-SHADE-RSP-MID",
                2,
                1.0000000000000005e-8,
                1e-8,
                1e-8,
                1e-8,
                1e-8,
                52837
            )
        )
    }
}