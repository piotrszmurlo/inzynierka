package com.inzynierka.ui

import io.kvision.toast.Toast
import io.kvision.toast.ToastOptions
import io.kvision.toast.ToastPosition
import io.kvision.utils.toFixedNoRound

fun Double.toPercentage(precision: Int): String {
    return (this * 100).toFixedNoRound(precision) + "%"
}

fun Toast.show(message: String) {
    danger(
        message,
        options = ToastOptions(ToastPosition.TOPLEFT, close = true, duration = 10000)
    )
}