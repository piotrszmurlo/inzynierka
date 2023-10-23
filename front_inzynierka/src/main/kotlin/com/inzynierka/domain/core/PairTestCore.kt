package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.domain.service.IDataService
import com.inzynierka.model.BenchmarkData
import io.kvision.redux.Dispatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


data class PairTestState(
    val result: String? = null,
    val algorithmNames: List<String> = listOf(),
    val dimensions: List<Int> = listOf(),
    val functionNumbers: List<Int> = listOf(),
    val formState: FormState = FormState()
)

data class FormState(
    val algorithmFirst: String? = null,
    val algorithmSecond: String? = null,
    val dimension: Int? = null,
    val functionNumber: Int? = null,
) {
    val isSubmitButtonDisabled: Boolean
        get() = algorithmFirst == algorithmSecond || algorithmFirst == null || dimension == null || functionNumber == null
}

sealed class PairTestAction : RankingsAction() {
    object PerformPairTest : PairTestAction()
    data class PairTestSuccess(val result: String) : PairTestAction()
    data class PairTestFailed(val error: DomainError?) : PairTestAction()

    object Initialize : PairTestAction()
    data class InitializeSuccess(val benchmarkData: BenchmarkData) : PairTestAction()
    data class InitializeFailed(val error: DomainError?) : PairTestAction()
    data class DimensionSelected(val dimension: Int) : PairTestAction()
    data class FunctionSelected(val functionNumber: Int) : PairTestAction()
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
                algorithmFirst = action.benchmarkData.algorithms[0],
                algorithmSecond = action.benchmarkData.algorithms[1],
                dimension = action.benchmarkData.dimensions[0],
                functionNumber = action.benchmarkData.functionNumbers[0]
            )
        )
    }

    is PairTestAction.InitializeFailed -> state

    is PairTestAction.PerformPairTest -> state.copy(result = null)
    is PairTestAction.PairTestFailed -> {
        state.copy(result = "Error")
    }

    is PairTestAction.PairTestSuccess -> state.copy(result = action.result)
    is PairTestAction.AlgorithmSelected -> state.copy(
        formState = state.formState.copy(
            algorithmFirst = action.algorithmFirst,
            algorithmSecond = action.algorithmSecond
        ),
        result = null
    )

    is PairTestAction.DimensionSelected -> state.copy(
        formState = state.formState.copy(dimension = action.dimension),
        result = null
    )

    is PairTestAction.FunctionSelected -> state.copy(
        formState = state.formState.copy(functionNumber = action.functionNumber),
        result = null
    )
}

fun performPairTest(
    dispatch: Dispatch<MainAppAction>,
    dataService: IDataService,
    algorithmFirst: String,
    algorithmSecond: String,
    dimension: Int,
    functionNumber: Int
) {
    dispatch(PairTestAction.PerformPairTest)
    CoroutineScope(Dispatchers.Default).launch {
        val result = dataService.getPairTest(
            algorithmFirst,
            algorithmSecond,
            dimension,
            functionNumber
        )
        when (result) {
            is Result.Success -> dispatch(PairTestAction.PairTestSuccess(result.data))
            is Result.Error -> dispatch(PairTestAction.PairTestFailed(result.domainError))
        }
    }
}

fun getAvailableBenchmarkData(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(PairTestAction.Initialize)
        when (val result = dataService.getAvailableBenchmarkData()) {
            is Result.Success -> dispatch(PairTestAction.InitializeSuccess(result.data))
            is Result.Error -> dispatch(PairTestAction.InitializeFailed(result.domainError))
        }
    }
}