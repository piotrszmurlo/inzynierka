package com.inzynierka.model

import kotlinx.serialization.Serializable

@Serializable
data class Data(val data: List<Int>)

@Serializable
data class CEC2022Data(val dimension: Map<String, List<ScoreEntry>>)

@Serializable
data class ScoreEntry(val algorithmName: String, val score: Double)