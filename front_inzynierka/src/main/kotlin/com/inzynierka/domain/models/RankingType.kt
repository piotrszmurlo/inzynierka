package com.inzynierka.domain.models

sealed class RankingType {
    object Averaged : RankingType()
    object PerFunction : RankingType()
}