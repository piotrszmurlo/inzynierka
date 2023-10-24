package com.inzynierka.data.models

import kotlinx.serialization.Serializable

typealias BasicScoresDTO = Map<Int, Map<Int, Map<String, StatisticEntry>>>

@Serializable
data class StatisticEntry(
    val median: Double,
    val mean: Double,
    val stddev: Double,
    val worst: Double,
    val best: Double,
)
