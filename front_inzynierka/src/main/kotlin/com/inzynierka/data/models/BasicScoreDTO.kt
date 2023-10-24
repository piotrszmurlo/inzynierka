package com.inzynierka.data.models

import com.inzynierka.domain.core.BasicScore
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class BasicScoreDTO(
    val dimension: Int,
    @SerialName("algorithm_name")
    val algorithmName: String,
    @SerialName("function_number")
    val functionNumber: Int,
    val mean: Double,
    val median: Double,
    val stddev: Double,
    val max: Double,
    val min: Double,
    @SerialName("min_fe_term")
    val minEvaluations: Int
)

fun BasicScoreDTO.toDomain() = BasicScore(
    rank = null,
    dimension = dimension,
    algorithmName = algorithmName,
    functionNumber = functionNumber,
    mean = mean,
    median = median,
    stddev = stddev,
    max = max,
    min = min,
    minEvaluations = minEvaluations
)


