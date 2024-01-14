package com.inzynierka.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserData(
    val email: String,
    val disabled: Boolean,
    @SerialName("is_admin")
    val isAdmin: Boolean
)