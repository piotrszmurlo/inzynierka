package com.inzynierka.ui

import com.inzynierka.domain.core.NumberNotation
import io.kvision.core.Border
import io.kvision.core.Container
import io.kvision.html.ButtonStyle
import io.kvision.html.tag
import io.kvision.i18n.tr
import io.kvision.toast.Toast
import io.kvision.toast.ToastOptions
import io.kvision.toast.ToastPosition
import io.kvision.utils.perc
import io.kvision.utils.px
import js.core.toExponential

fun tabButtonStyle(isActive: Boolean): ButtonStyle {
    return if (isActive) ButtonStyle.PRIMARY else ButtonStyle.OUTLINEPRIMARY
}

fun Toast.show(message: String) {
    console.log(tr("Select all CEC'22 results files for one algorithm to upload"))
    danger(
        message.substringAfterLast("###"),
        options = ToastOptions(ToastPosition.TOPLEFT, close = true, duration = 10000)
    )
}

fun Container.divider() {
    tag(type = io.kvision.html.TAG.HR) {
        border = Border(width = 5.px)
        this.color = io.kvision.core.Color.rgb(0, 0, 0)
        this.width = 100.perc
        this.height = 1.px
    }
}

fun Double.toPercentage(precision: Int): String {
    return (this * 100).toExponential(precision - 1).toDouble().toString() + "%"
}

fun Double.format(notation: NumberNotation, precision: Int): String {
    return when (notation) {
        is NumberNotation.Decimal -> {
            this.toExponential(precision - 1).toDouble().toString()
        }

        is NumberNotation.Scientific -> {
            this.toExponential(precision - 1)
        }
    }
}
