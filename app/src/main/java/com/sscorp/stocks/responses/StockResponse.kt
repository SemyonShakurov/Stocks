package com.sscorp.stocks.responses

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Ответ на запрос информации об акциях компании
@Serializable
data class StockResponse(
    @SerialName("c")
    val currentPrice: Double,
    @SerialName("pc")
    val previousPrice: Double
)