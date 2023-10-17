package com.inzynierka.model

import kotlinx.serialization.Serializable

@Serializable
data class Data(val data: List<Int>)

@Serializable
data class RemoteCEC2022Data(val dimension: Map<Int, List<ScoreEntry>>)

@Serializable
data class ScoreEntry(val algorithmName: String, val score: Double)
