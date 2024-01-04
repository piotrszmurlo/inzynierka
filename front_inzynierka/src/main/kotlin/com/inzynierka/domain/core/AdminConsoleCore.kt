package com.inzynierka.domain.core

import com.inzynierka.common.DomainError

data class AdminConsoleState(
    val algorithmNames: List<String> = listOf(),
    val selectedAlgorithmName: String? = null,
    val benchmarkNames: List<String> = listOf(),
    val selectedBenchmarkName: String? = null,
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
    val deleteAlgorithmButtonDisabled = algorithmNames.isEmpty() || benchmarkNames.isEmpty()
    val deleteBenchmarkButtonDisabled = benchmarkNames.isEmpty()
}

sealed class AdminConsoleAction : MainAppAction() {
    object FetchAlgorithmsStarted : AdminConsoleAction()
    object FetchAlgorithmsFailed : AdminConsoleAction()
    data class FetchAlgorithmsSuccess(val algorithmNames: List<String>) : AdminConsoleAction()

    object FetchBenchmarksFailed : AdminConsoleAction()
    data class FetchBenchmarksSuccess(val benchmarkNames: List<String>) : AdminConsoleAction()
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
            algorithmNames = action.algorithmNames,
            selectedAlgorithmName = action.algorithmNames.firstOrNull(),
            isFetching = false
        )
    }

    is AdminConsoleAction.FetchBenchmarksFailed -> state.copy(isFetching = false)
    is AdminConsoleAction.FetchBenchmarksSuccess -> {
        state.copy(
            benchmarkNames = action.benchmarkNames,
            selectedBenchmarkName = action.benchmarkNames.firstOrNull(),
            selectedBenchmarkNameToDelete = action.benchmarkNames.firstOrNull(),
            isFetching = false
        )
    }

    is AdminConsoleAction.AlgorithmSelected -> state.copy(selectedAlgorithmName = action.algorithmName)
    is AdminConsoleAction.BenchmarkSelected -> state.copy(selectedBenchmarkName = action.benchmarkName)
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