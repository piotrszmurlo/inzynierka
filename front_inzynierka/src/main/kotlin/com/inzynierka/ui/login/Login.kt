package com.inzynierka.ui.login

import com.inzynierka.domain.core.LoginAction
import com.inzynierka.domain.core.LoginState
import com.inzynierka.ui.AppManager
import com.inzynierka.ui.StringResources.EMAIL_PLACEHOLDER
import com.inzynierka.ui.StringResources.LOGIN_LABEL
import com.inzynierka.ui.StringResources.PASSWORD
import com.inzynierka.ui.StringResources.REGISTER_LABEL
import com.inzynierka.ui.StringResources.REGISTER_LABEL_PROMPT
import com.inzynierka.ui.divider
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.onChange
import io.kvision.form.ValidationStatus
import io.kvision.form.text.textInput
import io.kvision.html.Autocomplete
import io.kvision.html.InputType
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.utils.px

private const val MAX_PASSWORD_LENGTH = 32
private const val MAX_EMAIL_LENGTH = 64
fun Container.login(state: LoginState) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
        paddingTop = 32.px
        h5(LOGIN_LABEL)
        divider()
        loginForm(state)
    }
}

fun Container.loginForm(state: LoginState) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER, spacing = 8) {
        textInput(
            maxlength = MAX_EMAIL_LENGTH,
            value = state.email
        ) {
            autocomplete = Autocomplete.USERNAME
            this.placeholder = EMAIL_PLACEHOLDER
            this.validationStatus = if (!state.emailValid) ValidationStatus.INVALID else null
            padding = 4.px
        }.onChange {
            AppManager.store.dispatch(LoginAction.EmailChanged(this.value))
        }

        textInput(
            type = InputType.PASSWORD,
            maxlength = MAX_PASSWORD_LENGTH,
            value = state.password
        ) {
            this.placeholder = PASSWORD
            this.autocomplete = Autocomplete.CURRENT_PASSWORD
            padding = 4.px
        }.onChange {
            AppManager.store.dispatch(LoginAction.PasswordChanged(this.value))
        }
        button(
            LOGIN_LABEL,
            disabled = state.loginButtonDisabled
        ).onClick {
            if (state.email != null && state.password != null) {
                AppManager.loginUser(state.email, state.password)
            }
        }
        h5(REGISTER_LABEL_PROMPT) {
            paddingTop = 16.px
        }
        button(
            REGISTER_LABEL,
            disabled = state.loginButtonDisabled
        ).onClick {
            if (state.email != null && state.password != null) {
                AppManager.registerUser(state.email, state.password)
            }
        }
    }
}
