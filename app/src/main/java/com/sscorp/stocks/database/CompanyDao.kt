package com.sscorp.stocks.database

import androidx.room.*

@Dao
interface CompanyDao {

    // Получение всех элементов из бд
    @Query("SELECT * FROM companies")
    fun getAll(): List<CompanyItem>

    // Добавление элемента в бд
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(companyItem: CompanyItem)

    // Добавление / удаление компании из избранных
    @Query("UPDATE companies SET is_favourite = :isFavourite WHERE name = :name")
    fun updateItem(name: String, isFavourite: Boolean)
}