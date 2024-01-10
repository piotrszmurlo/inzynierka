package com.inzynierka.domain.models

data class Benchmark(
    val name: String,
    val description: String,
    val functionCount: Int,
    val trialCount: Int
)
