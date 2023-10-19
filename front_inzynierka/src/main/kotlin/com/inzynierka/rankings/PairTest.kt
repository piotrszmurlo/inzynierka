package com.inzynierka.rankings

import io.kvision.core.AlignItems
import io.kvision.core.Container
import io.kvision.core.FlexDirection
import io.kvision.core.JustifyContent
import io.kvision.form.check.radioGroup
import io.kvision.form.formPanel
import io.kvision.form.select.select
import io.kvision.html.Align
import io.kvision.html.p
import io.kvision.panel.flexPanel
import kotlinx.serialization.Serializable


@Serializable
data class PairTestForm(
    val algorithmFirst: String? = null,
    val algorithmSecond: String? = null,
    val dimension: String? = null
)

fun Container.pairTest(algorithmNames: List<String>, dimensions: List<Int>) {
    formPanel<PairTestForm> {
        flexPanel(
            direction = FlexDirection.COLUMN,
            justify = JustifyContent.CENTER,
        ) {
            alignItems = AlignItems.CENTER
            p(content = "Select algorithms to compare", align = Align.CENTER)
            flexPanel(direction = FlexDirection.ROW) {
                justifyContent = JustifyContent.CENTER
                select(
                    options = algorithmNames.map { it to it }
                ).bind(PairTestForm::algorithmFirst)
                select(
                    options = algorithmNames.map { it to it }
                ).bind(PairTestForm::algorithmSecond)
            }

            p(content = "Select dimension", align = Align.CENTER)
            radioGroup(
                options = dimensions.map { "$it" to "$it" },
                inline = true,
                value = "${dimensions.first()}"
            ).bind(PairTestForm::dimension)
        }
    }
}