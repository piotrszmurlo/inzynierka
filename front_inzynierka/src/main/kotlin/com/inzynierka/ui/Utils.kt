package com.inzynierka.ui

import io.kvision.utils.toFixedNoRound

fun Double.toPercentage(precision: Int): String {
    return (this * 100).toFixedNoRound(precision) + "%"
}