package com.inzynierka.domain

import com.inzynierka.domain.service.IDataService
import io.kvision.redux.Dispatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


data class PairTestState(
    val result: String = ""
)

sealed class PairTestAction : RankingsAction() {
    object PerformPairTest : PairTestAction()
    data class PairTestSuccess(val result: String) : PairTestAction()
    data class PairTestFailed(val error: DomainError?) : PairTestAction()
}

fun pairTestReducer(state: PairTestState, action: PairTestAction) = when (action) {
    is PairTestAction.PerformPairTest -> state
    is PairTestAction.PairTestFailed -> state
    is PairTestAction.PairTestSuccess -> state
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