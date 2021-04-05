package com.sscorp.stocks.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Ответ на запрос профиля компании
@Serializable
data class ProfileResponse(
    @SerialName("name")
    val name: String,
    @SerialName("logo")
    val logo: String
)