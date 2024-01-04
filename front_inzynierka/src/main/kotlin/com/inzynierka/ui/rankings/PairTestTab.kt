package com.inzynierka.ui.rankings

import com.inzynierka.domain.core.PairTestAction
import com.inzynierka.domain.core.PairTestState
import com.inzynierka.domain.models.PairTestEntry
import com.inzynierka.ui.AppManager
import com.inzynierka.ui.StringResources.ALGORITHM
import com.inzynierka.ui.StringResources.COMPARE
import com.inzynierka.ui.StringResources.EQUAL
import com.inzynierka.ui.StringResources.FUNCTION_NUMBER
import com.inzynierka.ui.StringResources.PAIR_TEST_TITLE
import com.inzynierka.ui.StringResources.RESULT
import com.inzynierka.ui.StringResources.RESULTS_TABLE
import com.inzynierka.ui.StringResources.SELECT_ALGORITHMS
import com.inzynierka.ui.StringResources.SELECT_DIMENSION
import com.inzynierka.ui.StringResources.SUMMED_UP_RESULTS
import com.inzynierka.ui.StringResources.SUMMED_UP_WINS
import com.inzynierka.ui.divider
import io.kvision.core.*
import io.kvision.form.check.radioGroup
import io.kvision.form.formPanel
import io.kvision.form.select.select
import io.kvision.html.Align
import io.kvision.html.button
import io.kvision.html.h5
import io.kvision.panel.flexPanel
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
    benchmarkName: String
) {
    flexPanel(
        direction = FlexDirection.COLUMN,
        alignItems = AlignItems.CENTER,
    ) {
        h5(content = PAIR_TEST_TITLE, align = Align.CENTER)
        divider()
        val formPanel = formPanel<PairTestForm> {
            flexPanel(
                direction = FlexDirection.COLUMN
            ) {
                alignItems = AlignItems.CENTER
                h5(content = SELECT_ALGORITHMS, align = Align.CENTER)
                flexPanel(direction = FlexDirection.ROW) {
                    justifyContent = JustifyContent.CENTER
                    select(
                        options = pairTestState.algorithmNames.map { it to it },
                        value = pairTestState.formState.algorithmFirst
                    ).bind(PairTestForm::algorithmFirst)
                        .onChange {
                            AppManager.store.dispatch(
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
                            AppManager.store.dispatch(
                                PairTestAction.AlgorithmSelected(
                                    algorithmFirst = form.getData().algorithmFirst!!,
                                    algorithmSecond = this.value!!
                                )
                            )
                        }
                }
                h5(content = SELECT_DIMENSION, align = Align.CENTER)
                radioGroup(
                    options = pairTestState.dimensions.map { "$it" to "$it" },
                    inline = true,
                    value = "${pairTestState.formState.dimension}"
                ).bind(PairTestForm::dimension)
                    .onChange { AppManager.store.dispatch(PairTestAction.DimensionSelected(this.value!!.toInt())) }
            }
        }

        button(text = COMPARE, disabled = pairTestState.formState.isSubmitButtonDisabled) {
            width = 150.px
            onClick {
                val formData = formPanel.getData()
                AppManager.performPairTest(
                    formData.algorithmFirst!!,
                    formData.algorithmSecond!!,
                    formData.dimension!!.toInt(),
                    benchmarkName
                )
            }
        }
        flexPanel(direction = FlexDirection.ROW, alignItems = AlignItems.START) {
            paddingTop = 48.px
            pairTestState.results?.let {
                pairTestTable(
                    headerNames = listOf(FUNCTION_NUMBER, RESULT),
                    title = RESULTS_TABLE,
                    results = it
                )
            }
            pairTestState.resultsSum?.let {
                pairTestSumTable(
                    headerNames = listOf(ALGORITHM, SUMMED_UP_WINS),
                    title = SUMMED_UP_RESULTS,
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
            results
                .sortedBy { it.functionNumber }
                .forEach {
                    row {
                        cell("${it.functionNumber}")
                        cell(it.winner ?: EQUAL)
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