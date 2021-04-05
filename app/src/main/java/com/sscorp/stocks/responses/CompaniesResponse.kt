package com.sscorp.stocks.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Ответ на запрос списка тикеров
@Serializable
data class CompaniesResponse(
    @SerialName("constituents")
    val constituents: List<String>
)