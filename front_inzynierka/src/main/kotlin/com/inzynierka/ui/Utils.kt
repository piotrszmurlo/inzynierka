package com.inzynierka.ui

import io.kvision.core.Border
import io.kvision.core.Container
import io.kvision.html.ButtonStyle
import io.kvision.html.tag
import io.kvision.toast.Toast
import io.kvision.toast.ToastOptions
import io.kvision.toast.ToastPosition
import io.kvision.utils.perc
import io.kvision.utils.px
import io.kvision.utils.toFixedNoRound

fun Double.toPercentage(precision: Int): String {
    return (this * 100).toFixedNoRound(precision) + "%"
}

fun tabButtonStyle(isActive: Boolean): ButtonStyle {
    return if (isActive) ButtonStyle.PRIMARY else ButtonStyle.OUTLINEPRIMARY
}

fun Toast.show(message: String) {
    danger(
        message,
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