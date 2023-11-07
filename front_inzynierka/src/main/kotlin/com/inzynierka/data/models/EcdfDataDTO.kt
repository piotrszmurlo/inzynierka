package com.inzynierka.data.models

import com.inzynierka.model.EcdfData
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class EcdfDataDTO(
    @SerialName("function_number")
    val functionNumber: Int,
    val dimension: Int,
    @SerialName("algorithm_name")
    val algorithmName: String,
    @SerialName("thresholds_achieved_fractions")
    val thresholdAchievedFractions: List<Double>,
    @SerialName("function_evaluations")
    val functionEvaluations: List<Double>,
)

fun EcdfDataDTO.toDomain() = EcdfData(
    functionNumber = functionNumber,
    dimension = dimension,
    algorithmName = algorithmName,
    thresholdAchievedFractions = thresholdAchievedFractions,
    functionEvaluations = functionEvaluations
)

