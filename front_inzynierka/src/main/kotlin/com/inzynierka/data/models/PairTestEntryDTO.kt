package com.inzynierka.data.models

import com.inzynierka.domain.models.PairTestEntry
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PairTestEntryDTO(
    @SerialName("function_number")
    val functionNumber: Int,
    val winner: String?
)

fun PairTestEntryDTO.toDomain() = PairTestEntry(
    functionNumber = this.functionNumber,
    winner = this.winner
)
