package com.inzynierka.domain.core

import com.inzynierka.common.DomainError
import io.kvision.redux.RAction
import org.koin.core.component.KoinComponent

sealed class Tab {
    object Upload : Tab()
    object AdminConsole : Tab()
    sealed class ResultsTab : Tab() {
        object PairTest : ResultsTab()
        object Cec2022 : ResultsTab()
        object Friedman : ResultsTab()
        object Median : ResultsTab()
        object Mean : ResultsTab()
        object Ecdf : ResultsTab()
        object Revisited : ResultsTab()
    }
}

data class MainAppState(
    val tab: Tab,
    val error: DomainError?,
    val uploadFilesState: UploadFilesState = UploadFilesState(),
    val rankingsState: RankingsState = RankingsState(),
    val adminConsoleState: AdminConsoleState = AdminConsoleState()
) : KoinComponent


sealed class MainAppAction : RAction {
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

    is RankingsAction -> state.copy(rankingsState = rankingsReducer(state.rankingsState, action))
    is AdminConsoleAction -> state.copy(adminConsoleState = adminConsoleReducer(state.adminConsoleState, action))
}
