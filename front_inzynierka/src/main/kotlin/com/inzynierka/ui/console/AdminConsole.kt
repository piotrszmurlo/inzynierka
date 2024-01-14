package com.inzynierka.ui.console

import com.inzynierka.domain.core.AdminConsoleAction
import com.inzynierka.domain.core.AdminConsoleState
import com.inzynierka.domain.core.DeleteAlgorithmFormState
import com.inzynierka.domain.core.LoginAction
import com.inzynierka.ui.AppManager
import com.inzynierka.ui.StringResources
import com.inzynierka.ui.StringResources.ADD_BENCHMARK_DESCRIPTION
import com.inzynierka.ui.StringResources.ADMIN_CONSOLE_LABEL
import com.inzynierka.ui.StringResources.ALGORITHM
import com.inzynierka.ui.StringResources.BENCHMARK
import com.inzynierka.ui.StringResources.BENCHMARK_DESCRIPTION
import com.inzynierka.ui.StringResources.BENCHMARK_NAME
import com.inzynierka.ui.StringResources.CREATE
import com.inzynierka.ui.StringResources.DELETE_ALGORITHM_BUTTON_LABEL
import com.inzynierka.ui.StringResources.DELETE_ALGORITHM_DESCRIPTION
import com.inzynierka.ui.StringResources.DELETE_BENCHMARK_BUTTON_LABEL
import com.inzynierka.ui.StringResources.DELETE_BENCHMARK_DESCRIPTION
import com.inzynierka.ui.StringResources.DESCRIPTION
import com.inzynierka.ui.StringResources.EMAIL
import com.inzynierka.ui.StringResources.FUNCTION_COUNT
import com.inzynierka.ui.StringResources.NAME
import com.inzynierka.ui.StringResources.PROMOTE_USERS_DESCRIPTION
import com.inzynierka.ui.StringResources.PROMOTE_USER_BUTTON_LABEL
import com.inzynierka.ui.StringResources.TRIAL_COUNT
import com.inzynierka.ui.divider
import com.inzynierka.ui.show
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.onChange
import io.kvision.form.formPanel
import io.kvision.form.number.numeric
import io.kvision.form.select.select
import io.kvision.form.text.text
import io.kvision.html.ButtonStyle
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.toast.Toast
import io.kvision.utils.px
import kotlinx.serialization.Serializable

@Serializable
data class BenchmarkForm(
    val name: String? = null,
    val description: String? = null,
    val functionCount: Int? = null,
    val trialCount: Int? = null
)

fun Container.adminConsole(state: AdminConsoleState) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER) {
        paddingTop = 32.px
        h5(ADMIN_CONSOLE_LABEL)
        divider()
        flexPanel(FlexDirection.ROW, alignItems = AlignItems.STRETCH, spacing = 32) {
            removeAlgorithmForm(
                state.deleteAlgorithmFormState,
                state.deleteAlgorithmButtonDisabled,
                onChangeBenchmark = {
                    AppManager.store.dispatch(
                        AdminConsoleAction.BenchmarkSelected(it)
                    )
                    AppManager.getAlgorithmNamesForBenchmark(
                        it,
                        actionOnsuccess = { AppManager.store.dispatch(AdminConsoleAction.FetchAlgorithmsSuccess(it)) },
                        actionOnFail = { AppManager.store.dispatch(AdminConsoleAction.FetchAlgorithmsFailed) }
                    )
                },
                onChangeAlgorithm = {
                    AppManager.store.dispatch(
                        AdminConsoleAction.AlgorithmSelected(
                            algorithmName = it,
                        )
                    )
                },
                deleteAlgorithmData = { algorithm, benchmark ->
                    AppManager.deleteAlgorithmData(
                        algorithm,
                        benchmark,
                        onSuccess = {
                            Toast.show(StringResources.TOAST_DELETE_ALGORITHM_DATA_SUCCESS)
                            AppManager.loadAdminConsole()
                            AppManager.store.dispatch(AdminConsoleAction.DeleteAlgorithmSuccess)
                        }, onError = {}
                    )
                }
            )
            deleteBenchmarkForm(state)
            promoteUsers(state)
            newBenchmarkForm(state)
        }
    }
}

fun Container.promoteUsers(state: AdminConsoleState) {
    flexPanel(FlexDirection.COLUMN, spacing = 8, alignItems = AlignItems.CENTER) {
        paddingTop = 32.px
        h5(PROMOTE_USERS_DESCRIPTION)
        val textField = text(
            value = state.userEmail,
            label = EMAIL,
        ) {
            paddingTop = 24.px
            width = 250.px
        }
        button(
            PROMOTE_USER_BUTTON_LABEL
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

fun Container.removeAlgorithmForm(
    state: DeleteAlgorithmFormState,
    buttonDisabled: Boolean,
    onChangeBenchmark: (String) -> Unit,
    onChangeAlgorithm: (String) -> Unit,
    deleteAlgorithmData: (String, String) -> Unit
) {
    flexPanel(FlexDirection.COLUMN, spacing = 8, alignItems = AlignItems.CENTER) {
        paddingTop = 32.px
        h5(DELETE_ALGORITHM_DESCRIPTION)
        select(
            options = state.benchmarkNames.map { it to it },
            value = state.selectedBenchmarkName,
            label = BENCHMARK
        ) {
            width = 250.px
        }.onChange {
            val benchmarkName = this.value!!
            onChangeBenchmark(benchmarkName)
        }
        select(
            options = state.algorithmNames.map { it to it },
            value = state.selectedAlgorithmName,
            label = ALGORITHM
        ) {
            width = 250.px
        }.onChange {
            onChangeAlgorithm(this.value!!)
        }
        button(
            DELETE_ALGORITHM_BUTTON_LABEL,
            style = ButtonStyle.DANGER,
            disabled = buttonDisabled
        ) {
            width = 250.px
        }.onClick {
            state.selectedAlgorithmName?.let { algorithmName ->
                state.selectedBenchmarkName?.let { benchmark ->
                    deleteAlgorithmData(algorithmName, benchmark)
                }
            }
        }
    }
}

fun Container.newBenchmarkForm(state: AdminConsoleState) {
    flexPanel(FlexDirection.COLUMN, alignItems = AlignItems.CENTER, spacing = 8) {
        paddingTop = 32.px
        h5(ADD_BENCHMARK_DESCRIPTION)
        val form = formPanel<BenchmarkForm> {
            text(
                label = BENCHMARK_NAME,
                value = state.newBenchmarkName,
                maxlength = 255
            ) {
                this.placeholder = NAME
                padding = 4.px
            }.bind(BenchmarkForm::name)

            text(
                label = BENCHMARK_DESCRIPTION,
                value = state.newBenchmarkDescription,
                maxlength = 255,
            ) {
                this.placeholder = DESCRIPTION
                padding = 4.px
            }.bind(BenchmarkForm::description)
            numeric(label = FUNCTION_COUNT, min = 1, decimals = 0).bind(BenchmarkForm::functionCount)
            numeric(label = TRIAL_COUNT, min = 1, decimals = 0).bind(BenchmarkForm::trialCount)
        }
        button(CREATE).onClick {
            val name = form.getData().name?.replace("/", "")
            val description = form.getData().description
            val functionCount = form.getData().functionCount
            val trialCount = form.getData().trialCount
            if (name != null && description != null && functionCount != null && trialCount != null) {
                AppManager.createBenchmark(name, description, functionCount.toInt(), trialCount.toInt())
            }
        }
    }
}


fun Container.deleteBenchmarkForm(state: AdminConsoleState) {
    flexPanel(FlexDirection.COLUMN, spacing = 8, alignItems = AlignItems.CENTER) {
        paddingTop = 32.px
        h5(DELETE_BENCHMARK_DESCRIPTION)
        select(
            options = state.deleteAlgorithmFormState.benchmarkNames.map { it to it },
            value = state.selectedBenchmarkNameToDelete,
            label = BENCHMARK
        ) {
            width = 250.px
        }.onChange {
            val benchmarkName = this.value!!
            AppManager.store.dispatch(
                AdminConsoleAction.BenchmarkDeleteSelected(benchmarkName)
            )
        }
        button(
            DELETE_BENCHMARK_BUTTON_LABEL,
            style = ButtonStyle.DANGER,
            disabled = state.deleteBenchmarkButtonDisabled
        ) {
            width = 250.px
        }.onClick {
            state.selectedBenchmarkNameToDelete?.let { benchmark ->
                AppManager.deleteBenchmark(
                    benchmark
                )
            }
        }
    }
}
