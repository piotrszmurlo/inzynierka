package com.inzynierka.ui.login

import com.inzynierka.domain.core.AccountSettingsAction
import com.inzynierka.domain.core.AccountSettingsState
import com.inzynierka.domain.core.isEmailValid
import com.inzynierka.domain.core.isPasswordValid
import com.inzynierka.ui.*
import com.inzynierka.ui.StringResources.TOAST_UPDATE_SUCCESS
import io.kvision.core.*
import io.kvision.form.text.text
import io.kvision.html.InputType
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.toast.Toast
import io.kvision.utils.px

fun Container.accountSettings(state: AccountSettingsState) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
        paddingTop = 32.px
        h5(StringResources.ACCOUNT_SETTINGS)
        divider()
        withLoadingSpinner(state.isFetching) {
            flexPanel(FlexDirection.ROW, alignItems = AlignItems.STRETCH, spacing = 32) {
                changePasswordForm(state)
                changeEmailForm(state)
            }
        }
        state.error?.let {
            Toast.show(StringResources.TOAST_FAILED_TO_CHANGE_PASSWORD(it.message))
            AppManager.store.dispatch(AccountSettingsAction.ResultHandled)
        }
        if (state.handleSuccess) {
            Toast.show(TOAST_UPDATE_SUCCESS)
            AppManager.store.dispatch(AccountSettingsAction.ResultHandled)
        }
    }
}

fun Container.changePasswordForm(state: AccountSettingsState) {
    flexPanel(FlexDirection.COLUMN, spacing = 8, alignItems = AlignItems.CENTER) {
        paddingTop = 32.px
        h5(StringResources.CHANGE_PASSWORD)
        val oldPasswordField = text(
            label = StringResources.OLD_PASSWORD,
            value = state.oldPasswordField,
            type = InputType.PASSWORD
        ) {
            paddingTop = 24.px
            width = 250.px
        }
        val newPasswordField = text(
            label = StringResources.NEW_PASSWORD,
            value = state.passwordField,
            type = InputType.PASSWORD
        ) {
            paddingTop = 24.px
            width = 250.px
            enableTooltip(
                TooltipOptions(
                    title = StringResources.PASSWORD_TOOLTIP,
                    placement = Placement.AUTO,
                    triggers = listOf(Trigger.HOVER, Trigger.MANUAL),
                    animation = false
                )
            )
        }
        button(
            StringResources.CHANGE_PASSWORD,
            disabled = state.isFetching
        ) {
            width = 250.px
        }.onClick {
            if (newPasswordField.value != null && isPasswordValid(newPasswordField.value) && oldPasswordField.value != null) {
                AppManager.changePassword(newPasswordField.value!!, oldPasswordField.value!!)
            }
        }
    }
}

fun Container.changeEmailForm(state: AccountSettingsState) {
    flexPanel(FlexDirection.COLUMN, spacing = 8, alignItems = AlignItems.CENTER) {
        paddingTop = 32.px
        h5(StringResources.CHANGE_EMAIL)
        val textField = text(
            label = StringResources.NEW_EMAIL,
            value = state.emailField,
            type = InputType.EMAIL,
        ) {
            paddingTop = 24.px
            width = 250.px
        }
        button(
            StringResources.CHANGE_EMAIL,
            disabled = state.isFetching
        ) {
            width = 250.px
        }.onClick {
            if (textField.value != null && isEmailValid(textField.value)) {
                AppManager.changeEmail(textField.value!!)
            }
        }
    }
}