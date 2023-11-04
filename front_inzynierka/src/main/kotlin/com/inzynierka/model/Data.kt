package com.inzynierka.model

import kotlinx.serialization.Serializable

@Serializable
data class BenchmarkData(val algorithms: List<String>, val dimensions: List<Int>, val functionNumbers: List<Int>)

