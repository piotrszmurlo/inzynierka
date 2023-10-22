package com.inzynierka.rankings

import com.inzynierka.domain.MainAppAction
import com.inzynierka.domain.MainAppState
import com.inzynierka.domain.performPairTest
import com.inzynierka.domain.service.IDataService
import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.JustifyContent
import io.kvision.form.check.radioGroup
import io.kvision.form.formPanel
import io.kvision.form.select.select
import io.kvision.html.Align
import io.kvision.html.button
import io.kvision.html.p
import io.kvision.panel.flexPanel
import io.kvision.redux.ReduxStore
import kotlinx.serialization.Serializable


@Serializable
data class PairTestForm(
    val algorithmFirst: String? = null,
    val algorithmSecond: String? = null,
    val dimension: String? = null,
    val functionNumber: String? = null
)

fun Container.pairTest(
    algorithmNames: List<String>,
    dimensions: List<Int>,
    store: ReduxStore<MainAppState, MainAppAction>,
    dataService: IDataService
) {
    flexPanel(
        direction = FlexDirection.COLUMN,
        justify = JustifyContent.CENTER,
    ) {
        val formPanel = formPanel<PairTestForm> {
            flexPanel(
                direction = FlexDirection.COLUMN,
                justify = JustifyContent.CENTER,
            ) {
                alignItems = AlignItems.CENTER
                p(content = "Select algorithms to compare", align = Align.CENTER)
                flexPanel(direction = FlexDirection.ROW) {
                    justifyContent = JustifyContent.CENTER
                    select(
                        options = algorithmNames.map { it to it },
                        value = algorithmNames.firstOrNull()
                    ).bind(PairTestForm::algorithmFirst)
                    select(
                        options = algorithmNames.map { it to it },
                        value = algorithmNames.lastOrNull()
                    ).bind(PairTestForm::algorithmSecond)
                }
                p(content = "Select function number", align = Align.CENTER)
                select(
                    options = listOf("1" to "1", "2" to "2"),
                    value = "1"
                ).bind(PairTestForm::functionNumber)
                p(content = "Select dimension", align = Align.CENTER)
                radioGroup(
                    options = dimensions.map { "$it" to "$it" },
                    inline = true,
                    value = "${dimensions.first()}"
                ).bind(PairTestForm::dimension)
            }
        }

        button("Compare") {
            onClick {
                val formData = formPanel.getData()
                store.dispatch { dispatch, _ ->
                    performPairTest(
                        dispatch,
                        dataService,
                        formData.algorithmFirst!!,
                        formData.algorithmSecond!!,
                        formData.dimension!!.toInt(),
                        formData.functionNumber!!.toInt()
                    )
                }
            }
        }
    }
}