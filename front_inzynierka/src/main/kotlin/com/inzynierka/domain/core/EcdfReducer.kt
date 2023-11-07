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
    data class FetchRankingsSuccess(val data: List<EcdfData>) : EcdfAction()
    data class FetchRankingsFailed(val error: DomainError?) : EcdfAction()
}

data class EcdfState(
    val isFetching: Boolean = false,
    val showFunctionGroups: Boolean = false,
    val data: Map<Dimension, Map<FunctionNumber, List<EcdfData>>>? = null,
    val combinedData: Map<Dimension, List<EcdfData>>? = null
)

fun ecdfReducer(state: EcdfState, action: EcdfAction) = when (action) {
    is EcdfAction.FetchRankingsFailed -> state.copy(isFetching = false)
    is EcdfAction.FetchRankingsStarted -> state.copy(isFetching = true)
    is EcdfAction.FetchRankingsSuccess -> state.copy(
        isFetching = false, data = splitEcdfs(action.data)
    )
}

fun splitEcdfs(data: List<EcdfData>): Map<Dimension, Map<FunctionNumber, List<EcdfData>>> {
    return data
        .groupBy { it.dimension }
        .mapValues {
            it.value.groupBy { data -> data.functionNumber }
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