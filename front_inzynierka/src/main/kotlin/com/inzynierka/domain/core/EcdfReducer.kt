package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import com.inzynierka.common.Result
import com.inzynierka.domain.service.IDataService
import com.inzynierka.model.EcdfData
import io.kvision.redux.Dispatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

sealed class EcdfAction : RankingsAction() {
    object FetchRankingsStarted : EcdfAction()
    object ToggleShowFunctionGroups : EcdfAction()
    data class FetchRankingsSuccess(val data: List<EcdfData>) : EcdfAction()
    data class FetchRankingsFailed(val error: DomainError?) : EcdfAction()
}


data class EcdfState(
    val isFetching: Boolean = false,
    val showFunctionGroups: Boolean = false,
    val data: Map<Dimension, Map<FunctionNumber, List<EcdfData>>>? = null,
    val combinedData: Map<Dimension, List<EcdfData>>? = null,
    val functionGroupData: Map<Dimension, Map<FunctionGroup, List<EcdfData>>>? = null
)

fun ecdfReducer(state: EcdfState, action: EcdfAction) = when (action) {
    is EcdfAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is EcdfAction.FetchRankingsStarted -> state.copy(isFetching = true)
    is EcdfAction.FetchRankingsSuccess -> {
        val combinedData = action.data.groupBy { it.dimension }
        state.copy(
            isFetching = false,
            data = splitEcdfs(action.data),
            combinedData = combinedData,
            functionGroupData = combinedData
                .mapValues {
                    it.value.groupBy { ecdfData -> getFunctionGroup(ecdfData.functionNumber) }
                }
        )
    }

    is EcdfAction.ToggleShowFunctionGroups -> state.copy(showFunctionGroups = !state.showFunctionGroups)
}

fun splitEcdfs(data: List<EcdfData>): Map<Dimension, Map<FunctionNumber, List<EcdfData>>> {
    return data
        .groupBy { it.dimension }
        .mapValues {
            it.value.groupBy { data -> data.functionNumber }
        }
}

enum class FunctionGroup(val functionNumbers: List<Int>) {
    UNIMODAL(listOf(1)),
    BASIC(listOf(2, 3, 4, 5)),
    HYBRID(listOf(6, 7, 8)),
    COMPOSITION(listOf(9, 10, 11, 12))
}

fun getFunctionGroup(functionNumber: FunctionNumber): FunctionGroup {
    return when (functionNumber) {
        in FunctionGroup.UNIMODAL.functionNumbers -> FunctionGroup.UNIMODAL
        in FunctionGroup.BASIC.functionNumbers -> FunctionGroup.BASIC
        in FunctionGroup.HYBRID.functionNumbers -> FunctionGroup.HYBRID
        in FunctionGroup.COMPOSITION.functionNumbers -> FunctionGroup.COMPOSITION
        else -> throw IllegalArgumentException()
    }
}

fun loadEcdfData(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(FriedmanRankingAction.FetchRankingsStarted)
        when (val result = dataService.getEcdfData()) {
            is Result.Success -> dispatch(EcdfAction.FetchRankingsSuccess(result.data))
            is Result.Error -> dispatch(EcdfAction.FetchRankingsFailed(result.domainError))
        }
    }
}