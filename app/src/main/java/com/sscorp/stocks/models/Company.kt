package com.sscorp.stocks.models

// Компания
data class Company(
    val name: String,
    val icon: String,
    val ticker: String,
    val cost: Double,
    val priceChange: Double,
    var isFavourite: Boolean = false,
)