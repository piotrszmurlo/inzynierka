package com.inzynierka.ui.console

import com.inzynierka.domain.core.AdminConsoleAction
import com.inzynierka.domain.core.AdminConsoleState
import com.inzynierka.domain.core.LoginAction
import com.inzynierka.ui.AppManager
import com.inzynierka.ui.StringResources.ADMIN_CONSOLE_LABEL
import com.inzynierka.ui.StringResources.DELETE_ALGORITHM_BUTTON_LABEL
import com.inzynierka.ui.StringResources.DELETE_ALGORITHM_DESCRIPTION
import com.inzynierka.ui.StringResources.PROMOTE_USERS_DESCRIPTION
import com.inzynierka.ui.StringResources.PROMOTE_USER_BUTTON_LABEL
import com.inzynierka.ui.divider
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.onChange
import io.kvision.form.select.select
import io.kvision.form.text.text
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.utils.px

fun Container.adminConsole(state: AdminConsoleState) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
        paddingTop = 32.px
        h5(ADMIN_CONSOLE_LABEL)
        divider()
        flexPanel(FlexDirection.ROW, alignItems = AlignItems.STRETCH, spacing = 16) {
            removeAlgorithmForm(state)
            promoteUsers(state)
        }
    }
}

fun Container.promoteUsers(state: AdminConsoleState) {
    flexPanel(FlexDirection.COLUMN, spacing = 8, alignItems = AlignItems.CENTER) {
        paddingTop = 32.px
        h5(PROMOTE_USERS_DESCRIPTION)
        val textField = text(
            value = state.userEmail
        ) {
            paddingTop = 24.px
            width = 250.px
        }
        button(
            PROMOTE_USER_BUTTON_LABEL,
            style = ButtonStyle.DANGER,
            disabled = state.deleteButtonDisabled
        ) {
            width = 250.px
        }.onClick {
            AppManager.store.dispatch(LoginAction.EmailChanged(textField.value))
            textField.value?.let { email ->
                AppManager.promoteUserToAdmin(email)
            }
        }
    }
}

fun Container.removeAlgorithmForm(state: AdminConsoleState) {
    flexPanel(FlexDirection.COLUMN, spacing = 8, alignItems = AlignItems.CENTER) {
        paddingTop = 32.px
        h5(DELETE_ALGORITHM_DESCRIPTION)
        select(
            options = state.algorithmNames.map { it to it },
            value = state.selectedAlgorithmName
        ) {
            width = 250.px
        }.onChange {
            AppManager.store.dispatch(
                AdminConsoleAction.AlgorithmSelected(
                    algorithmName = this.value!!,
                )
            )
        }
        button(
            DELETE_ALGORITHM_BUTTON_LABEL,
            style = ButtonStyle.DANGER,
            disabled = state.deleteButtonDisabled
        ) {
            width = 250.px
        }.onClick {
            state.selectedAlgorithmName?.let { algorithmName ->
                AppManager.deleteAlgorithmData(algorithmName)
            }
        }
    }
}