package com.inzynierka.domain.core

import io.kvision.redux.RAction
import org.koin.core.component.KoinComponent

sealed class Tab {
    object Upload : Tab()
    object Login : Tab()
    object AccountSettings : Tab()
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
    val uploadFilesState: UploadFilesState = UploadFilesState(),
    val rankingsState: RankingsState = RankingsState(),
    val adminConsoleState: AdminConsoleState = AdminConsoleState(),
    val loginState: LoginState = LoginState(),
    val accountSettingsState: AccountSettingsState = AccountSettingsState()
) : KoinComponent {
    val isUserLoggedIn = loginState.isUserLoggedIn
    val isUserVerified = loginState.loggedInUserData?.disabled == false
}

sealed class MainAppAction : RAction {
    data class TabSelected(val tab: Tab) : MainAppAction()
}

fun mainAppReducer(state: MainAppState, action: MainAppAction): MainAppState = when (action) {
    is UploadAction -> state.copy(uploadFilesState = uploadReducer(state.uploadFilesState, action))
    is RankingsAction -> state.copy(rankingsState = rankingsReducer(state.rankingsState, action))
    is AdminConsoleAction -> state.copy(adminConsoleState = adminConsoleReducer(state.adminConsoleState, action))
    is LoginAction -> state.copy(loginState = loginReducer(state.loginState, action))
    is MainAppAction.TabSelected -> state.copy(
        tab = action.tab,
        rankingsState = state.rankingsState
    )

    is AccountSettingsAction -> state.copy(
        accountSettingsState = accountSettingsReducer(
            state.accountSettingsState,
            action
        )
    )
}
