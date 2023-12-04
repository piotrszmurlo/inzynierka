package com.inzynierka.ui.login

import com.inzynierka.domain.core.LoginAction
import com.inzynierka.domain.core.LoginState
import com.inzynierka.domain.core.isEmailValid
import com.inzynierka.domain.core.isPasswordValid
import com.inzynierka.ui.AppManager
import com.inzynierka.ui.StringResources.EMAIL_PLACEHOLDER
import com.inzynierka.ui.StringResources.LOGIN_LABEL
import com.inzynierka.ui.StringResources.PASSWORD
import com.inzynierka.ui.StringResources.PASSWORD_TOOLTIP
import com.inzynierka.ui.StringResources.REGISTER_LABEL
import com.inzynierka.ui.StringResources.REGISTER_LABEL_PROMPT
import com.inzynierka.ui.divider
import io.kvision.core.*
import io.kvision.form.ValidationStatus
import io.kvision.form.formPanel
import io.kvision.form.text.text
import io.kvision.html.Autocomplete
import io.kvision.html.InputType
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.utils.px
import kotlinx.serialization.Serializable

private const val MAX_PASSWORD_LENGTH = 32
private const val MAX_EMAIL_LENGTH = 64

@Serializable
data class LoginForm(
    val username: String? = null,
    val password: String? = null,
)

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
        val form = formPanel<LoginForm> {
            text(
                value = state.email,
                maxlength = MAX_EMAIL_LENGTH
            ) {
                autocomplete = Autocomplete.USERNAME
                this.placeholder = EMAIL_PLACEHOLDER
                this.validationStatus = if (!state.emailValid) ValidationStatus.INVALID else null
                padding = 4.px
            }.bind(LoginForm::username)

            text(
                type = InputType.PASSWORD,
                value = state.password,
                maxlength = MAX_PASSWORD_LENGTH,
            ) {
                this.validationStatus = if (!state.passwordValid) ValidationStatus.INVALID else null
                this.placeholder = PASSWORD
                this.autocomplete = Autocomplete.CURRENT_PASSWORD
                padding = 4.px
                enableTooltip(
                    TooltipOptions(
                        title = PASSWORD_TOOLTIP,
                        placement = Placement.RIGHT,
                        triggers = listOf(Trigger.HOVER)
                    )
                )
            }.bind(LoginForm::password)
        }

        button(
            LOGIN_LABEL
        ).onClick {
            val email = form.getData().username
            val password = form.getData().password
            AppManager.store.dispatch(LoginAction.EmailChanged(email))
            AppManager.store.dispatch(LoginAction.PasswordChanged(password))
            if (email != null && password != null && isEmailValid(email) && isPasswordValid(password)) {
                AppManager.loginUser(email, password)
            }
        }
        h5(REGISTER_LABEL_PROMPT) {
            paddingTop = 16.px
        }
        button(
            REGISTER_LABEL
        ).onClick {
            val email = form.getData().username
            val password = form.getData().password
            AppManager.store.dispatch(LoginAction.EmailChanged(email))
            AppManager.store.dispatch(LoginAction.PasswordChanged(password))
            if (email != null && password != null && isEmailValid(email) && isPasswordValid(password)) {
                AppManager.registerUser(email, password)
            }

        }
    }
}
