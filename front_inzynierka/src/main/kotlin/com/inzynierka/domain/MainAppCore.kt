package com.inzynierka.domain

import com.inzynierka.domain.service.IDataService
import io.kvision.redux.Dispatch
import io.kvision.redux.RAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

sealed class Tab {
    object Upload : Tab()
    sealed class ResultsTab : Tab() {
        object PairTest : ResultsTab()
        object CEC2022 : ResultsTab()
        object Friedman : ResultsTab()
        object Median : ResultsTab()
        object Mean : ResultsTab()
        object ECDF : ResultsTab()
    }
}

data class MainAppState(
    val tab: Tab,
    val error: DomainError?,
    val uploadFilesState: UploadFilesState = UploadFilesState(),
    val availableAlgorithms: List<String> = listOf(),
    val availableDimensions: List<Int> = listOf(),
    val rankingsState: RankingsState = RankingsState()
) : KoinComponent


sealed class MainAppAction : RAction {

    object FetchAlgorithmNamesStarted : MainAppAction()
    data class FetchAlgorithmNamesSuccess(val names: List<String>) : MainAppAction()
    data class FetchAlgorithmNamesFailed(val error: DomainError?) : MainAppAction()
    object ErrorHandled : MainAppAction()
    data class TabSelected(val tab: Tab) : MainAppAction()
}


fun mainAppReducer(state: MainAppState, action: MainAppAction): MainAppState = when (action) {

    is UploadAction -> state.copy(uploadFilesState = uploadReducer(state.uploadFilesState, action))

    is MainAppAction.ErrorHandled -> state.copy(error = null)

    is MainAppAction.TabSelected -> state.copy(
        tab = action.tab,
        rankingsState = state.rankingsState
    )

    is MainAppAction.FetchAlgorithmNamesStarted -> state
    is MainAppAction.FetchAlgorithmNamesFailed -> state
    is MainAppAction.FetchAlgorithmNamesSuccess -> state.copy(availableAlgorithms = action.names)

    is RankingsAction -> state.copy(rankingsState = rankingsReducer(state.rankingsState, action))
}


fun loadAvailableAlgorithms(dispatch: Dispatch<MainAppAction>, dataService: IDataService) {
    CoroutineScope(Dispatchers.Default).launch {
        dispatch(MainAppAction.FetchAlgorithmNamesStarted)
        when (val result = dataService.getAvailableAlgorithms()) {
            is Result.Success -> dispatch(MainAppAction.FetchAlgorithmNamesSuccess(result.data))
            is Result.Error -> dispatch(MainAppAction.FetchAlgorithmNamesFailed(result.domainError))
        }
    }
}

