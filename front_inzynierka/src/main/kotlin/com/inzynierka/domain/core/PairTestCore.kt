package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.domain.models.PairTestEntry
import com.inzynierka.model.BenchmarkData

private const val FIRST_WON = "-"
private const val SECOND_WON = "+"
private const val EQUAL = "="


data class PairTestState(
    val results: List<PairTestEntry>? = null,
    val resultsSum: Map<String, Int>? = null,
    val algorithmNames: List<String> = listOf(),
    val dimensions: List<Int> = listOf(),
    val functionNumbers: List<Int> = listOf(),
    val formState: FormState = FormState()
)

data class FormState(
    val algorithmFirst: String? = null,
    val algorithmSecond: String? = null,
    val dimension: Int? = null
) {
    val isSubmitButtonDisabled: Boolean
        get() = algorithmFirst == algorithmSecond || algorithmFirst == null || dimension == null
}

sealed class PairTestAction : RankingsAction() {
    object PerformPairTest : PairTestAction()
    data class PairTestSuccess(val results: List<PairTestEntry>) : PairTestAction()
    data class PairTestFailed(val error: DomainError?) : PairTestAction()

    object Initialize : PairTestAction()
    data class InitializeSuccess(val benchmarkData: BenchmarkData) : PairTestAction()
    data class InitializeFailed(val error: DomainError?) : PairTestAction()
    data class DimensionSelected(val dimension: Int) : PairTestAction()
    data class AlgorithmSelected(val algorithmFirst: String, val algorithmSecond: String) : PairTestAction()
}

fun pairTestReducer(state: PairTestState, action: PairTestAction) = when (action) {
    is PairTestAction.Initialize -> state
    is PairTestAction.InitializeSuccess -> {
        state.copy(
            algorithmNames = action.benchmarkData.algorithms,
            dimensions = action.benchmarkData.dimensions,
            functionNumbers = action.benchmarkData.functionNumbers,
            formState = FormState(
                algorithmFirst = action.benchmarkData.algorithms.firstOrNull(),
                algorithmSecond = action.benchmarkData.algorithms.lastOrNull(),
                dimension = action.benchmarkData.dimensions.firstOrNull()
            ),
            results = null,
            resultsSum = null
        )
    }

    is PairTestAction.InitializeFailed -> state

    is PairTestAction.PerformPairTest -> state.copy(
        results = null,
        resultsSum = null
    )

    is PairTestAction.PairTestFailed -> {
        state
    }

    is PairTestAction.PairTestSuccess -> {
        val resultsSum = mutableMapOf(
            state.formState.algorithmFirst!! to 0,
            state.formState.algorithmSecond!! to 0,
        )
        state.copy(
            results = action.results.map {
                when (it.winner) {
                    state.formState.algorithmFirst -> {
                        resultsSum[state.formState.algorithmFirst] = resultsSum[state.formState.algorithmFirst]!! + 1
                        it.copy(winner = FIRST_WON)
                    }

                    state.formState.algorithmSecond -> {
                        resultsSum[state.formState.algorithmSecond] = resultsSum[state.formState.algorithmSecond]!! + 1
                        it.copy(winner = SECOND_WON)
                    }

                    else -> it.copy(winner = EQUAL)
                }
            },
            resultsSum = resultsSum.toMap()
        )
    }

    is PairTestAction.AlgorithmSelected -> state.copy(
        formState = state.formState.copy(
            algorithmFirst = action.algorithmFirst,
            algorithmSecond = action.algorithmSecond
        ),
        results = null,
        resultsSum = null
    )

    is PairTestAction.DimensionSelected -> state.copy(
        formState = state.formState.copy(dimension = action.dimension),
        results = null,
        resultsSum = null
    )
}



