package com.sscorp.stocks.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

// Элемент таблицы
@Entity(tableName = "companies")
data class CompanyItem(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "icon")
    val icon: String,

    @ColumnInfo(name = "ticker")
    val ticker: String,

    @ColumnInfo(name = "is_favourite")
    var isFavourite: Boolean = false,
)