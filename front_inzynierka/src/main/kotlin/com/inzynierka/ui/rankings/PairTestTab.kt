package com.inzynierka.ui.rankings

import com.inzynierka.domain.NetworkActions
import com.inzynierka.domain.core.MainAppAction
import com.inzynierka.domain.core.MainAppState
import com.inzynierka.domain.core.PairTestAction
import com.inzynierka.domain.core.PairTestState
import com.inzynierka.domain.models.PairTestEntry
import io.kvision.core.*
import io.kvision.form.check.radioGroup
import io.kvision.form.formPanel
import io.kvision.form.select.select
import io.kvision.html.Align
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
import io.kvision.redux.ReduxStore
import io.kvision.table.TableType
import io.kvision.table.cell
import io.kvision.table.row
import io.kvision.table.table
import io.kvision.utils.px
import kotlinx.serialization.Serializable


@Serializable
data class PairTestForm(
    val algorithmFirst: String? = null,
    val algorithmSecond: String? = null,
    val dimension: String? = null
)

fun Container.pairTest(
    pairTestState: PairTestState,
    store: ReduxStore<MainAppState, MainAppAction>,
    networkActions: NetworkActions
) {
    flexPanel(
        direction = FlexDirection.COLUMN,
        justify = JustifyContent.CENTER,
        alignItems = AlignItems.CENTER
    ) {
        h5(content = "Compare algorithm using Wilcoxon signed-rank test", align = Align.CENTER)
        val formPanel = formPanel<PairTestForm> {
            flexPanel(
                direction = FlexDirection.COLUMN,
                justify = JustifyContent.CENTER,
            ) {
                alignItems = AlignItems.CENTER
                h5(content = "Select algorithms", align = Align.CENTER)
                flexPanel(direction = FlexDirection.ROW) {
                    justifyContent = JustifyContent.CENTER
                    select(
                        options = pairTestState.algorithmNames.map { it to it },
                        value = pairTestState.formState.algorithmFirst
                    ).bind(PairTestForm::algorithmFirst)
                        .onChange {
                            store.dispatch(
                                PairTestAction.AlgorithmSelected(
                                    algorithmFirst = this.value!!,
                                    algorithmSecond = form.getData().algorithmSecond!!
                                )
                            )
                        }
                    select(
                        options = pairTestState.algorithmNames.map { it to it },
                        value = pairTestState.formState.algorithmSecond
                    ).bind(PairTestForm::algorithmSecond)
                        .onChange {
                            store.dispatch(
                                PairTestAction.AlgorithmSelected(
                                    algorithmFirst = form.getData().algorithmFirst!!,
                                    algorithmSecond = this.value!!
                                )
                            )
                        }
                }
                h5(content = "Select dimension", align = Align.CENTER)
                radioGroup(
                    options = pairTestState.dimensions.map { "$it" to "$it" },
                    inline = true,
                    value = "${pairTestState.formState.dimension}"
                ).bind(PairTestForm::dimension)
                    .onChange { store.dispatch(PairTestAction.DimensionSelected(this.value!!.toInt())) }
            }
        }

        button(text = "Compare", disabled = pairTestState.formState.isSubmitButtonDisabled) {
            width = 150.px
            onClick {
                val formData = formPanel.getData()
                store.dispatch { dispatch, _ ->
                    networkActions.performPairTest(
                        dispatch,
                        formData.algorithmFirst!!,
                        formData.algorithmSecond!!,
                        formData.dimension!!.toInt()
                    )
                }
            }
        }
        flexPanel(direction = FlexDirection.ROW, alignItems = AlignItems.START) {
            paddingTop = 48.px
            pairTestState.results?.let {
                pairTestTable(
                    headerNames = listOf("Function number", "Result"),
                    title = "Results table",
                    results = it
                )
            }
            pairTestState.resultsSum?.let {
                pairTestSumTable(
                    headerNames = listOf("Algorithm", "Sum of wins"),
                    title = "Summed results",
                    resultsSum = it
                )
            }
        }
    }
}

fun Container.pairTestTable(
    headerNames: List<String>,
    title: String,
    results: List<PairTestEntry>
) {
    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
        padding = 16.px
        h5(content = title, align = Align.CENTER)
        table(
            headerNames = headerNames,
            types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
        ) {
            results.forEach {
                row {
                    cell("${it.functionNumber}")
                    cell(it.winner ?: "Equal")
                }
            }
        }
    }
}

fun Container.pairTestSumTable(
    headerNames: List<String>,
    title: String,
    resultsSum: Map<String, Int>
) {
    flexPanel(FlexDirection.COLUMN, justify = JustifyContent.CENTER) {
        padding = 16.px
        h5(content = title, align = Align.CENTER)
        table(
            headerNames = headerNames,
            types = setOf(TableType.BORDERED, TableType.STRIPED, TableType.HOVER),
        ) {
            resultsSum.forEach { (algorithmName, sumOfWins) ->
                row {
                    cell(algorithmName)
                    cell("$sumOfWins")
                }
            }
        }
    }
}