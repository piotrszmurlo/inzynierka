package com.inzynierka.model

data class EcdfData(
    val functionNumber: Int,
    val dimension: Int,
    val algorithmName: String,
    val thresholdAchievedFractions: List<Double>,
    val functionEvaluations: List<Double>
)
