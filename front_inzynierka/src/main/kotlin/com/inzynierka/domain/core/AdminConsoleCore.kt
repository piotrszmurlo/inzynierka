package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.domain.models.Benchmark

data class AdminConsoleState(
    val deleteAlgorithmFormState: DeleteAlgorithmFormState = DeleteAlgorithmFormState(),
    val selectedBenchmarkNameToDelete: String? = null,
    val userEmail: String? = null,
    val error: DomainError? = null,
    val isDeleting: Boolean? = null,
    val isFetching: Boolean? = null,
    val newBenchmarkName: String? = null,
    val newBenchmarkDescription: String? = null,
    val newBenchmarkFunctionCount: Int? = null,
    val newBenchmarkTrialCount: Int? = null,
) {
    val deleteAlgorithmButtonDisabled =
        deleteAlgorithmFormState.algorithmNames.isEmpty() || deleteAlgorithmFormState.benchmarkNames.isEmpty()
    val deleteBenchmarkButtonDisabled = deleteAlgorithmFormState.benchmarkNames.isEmpty()
}

sealed class AdminConsoleAction : MainAppAction() {
    object FetchAlgorithmsStarted : AdminConsoleAction()
    object FetchAlgorithmsFailed : AdminConsoleAction()
    data class FetchAlgorithmsSuccess(val algorithmNames: List<String>) : AdminConsoleAction()

    object FetchBenchmarksFailed : AdminConsoleAction()
    data class FetchBenchmarksSuccess(val benchmark: List<Benchmark>) : AdminConsoleAction()
    object PromoteUserStarted : AdminConsoleAction()
    object PromoteUserSuccess : AdminConsoleAction()
    object PromoteUserFailed : AdminConsoleAction()

    object VerifyUserStarted : AdminConsoleAction()
    object VerifyUserSuccess : AdminConsoleAction()
    object VerifyUserFailed : AdminConsoleAction()
    data class AlgorithmSelected(val algorithmName: String) : AdminConsoleAction()
    data class BenchmarkSelected(val benchmarkName: String) : AdminConsoleAction()
    data class BenchmarkDeleteSelected(val benchmarkName: String) : AdminConsoleAction()
    object DeleteAlgorithmStarted : AdminConsoleAction()
    object DeleteAlgorithmFailed : AdminConsoleAction()
    object DeleteAlgorithmSuccess : AdminConsoleAction()
    object CreateBenchmarkFailed : AdminConsoleAction()
    object CreateBenchmarkSuccess : AdminConsoleAction()


}

fun adminConsoleReducer(state: AdminConsoleState, action: AdminConsoleAction) = when (action) {
    is AdminConsoleAction.FetchAlgorithmsStarted -> state.copy(isFetching = true)
    is AdminConsoleAction.FetchAlgorithmsFailed -> state.copy(isFetching = false)
    is AdminConsoleAction.FetchAlgorithmsSuccess -> {
        state.copy(
            deleteAlgorithmFormState = state.deleteAlgorithmFormState.copy(
                algorithmNames = action.algorithmNames,
                selectedAlgorithmName = action.algorithmNames.firstOrNull(),
            ),
            isFetching = false
        )
    }

    is AdminConsoleAction.FetchBenchmarksFailed -> state.copy(isFetching = false)
    is AdminConsoleAction.FetchBenchmarksSuccess -> {
        state.copy(
            deleteAlgorithmFormState = state.deleteAlgorithmFormState.copy(
                benchmarkNames = action.benchmark.map { it.name },
                selectedBenchmarkName = action.benchmark.firstOrNull()?.name,
            ),
            selectedBenchmarkNameToDelete = action.benchmark.firstOrNull()?.name,
            isFetching = false
        )
    }

    is AdminConsoleAction.AlgorithmSelected -> state.copy(
        deleteAlgorithmFormState = state.deleteAlgorithmFormState.copy(
            selectedAlgorithmName = action.algorithmName
        )
    )

    is AdminConsoleAction.BenchmarkSelected -> state.copy(
        deleteAlgorithmFormState = state.deleteAlgorithmFormState.copy(
            selectedBenchmarkName = action.benchmarkName
        )
    )

    is AdminConsoleAction.DeleteAlgorithmFailed -> state.copy(isDeleting = false)
    is AdminConsoleAction.DeleteAlgorithmStarted -> state.copy(isDeleting = false)
    is AdminConsoleAction.DeleteAlgorithmSuccess -> state.copy(isDeleting = true)
    is AdminConsoleAction.PromoteUserFailed -> state.copy(isFetching = false)
    is AdminConsoleAction.PromoteUserStarted -> state.copy(isFetching = true)
    is AdminConsoleAction.PromoteUserSuccess -> state.copy(isFetching = false)
    is AdminConsoleAction.VerifyUserFailed -> state.copy()
    is AdminConsoleAction.VerifyUserStarted -> state.copy()
    is AdminConsoleAction.VerifyUserSuccess -> state.copy()
    is AdminConsoleAction.CreateBenchmarkFailed -> state.copy()
    is AdminConsoleAction.CreateBenchmarkSuccess -> state.copy()
    is AdminConsoleAction.BenchmarkDeleteSelected -> state.copy(selectedBenchmarkNameToDelete = action.benchmarkName)

}