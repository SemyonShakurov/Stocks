package com.sscorp.stocks.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sscorp.stocks.App

// База данных для хранения информации о компаниях
@Database(entities = [CompanyItem::class], version = 1)
abstract class AppDataBase : RoomDatabase() {

    abstract fun companyDao(): CompanyDao

    companion object {
        private const val DATABASE_NAME = "companies.db"

        val instance: AppDataBase by lazy {
            Room.databaseBuilder(
                App.getAppContext(),
                AppDataBase::class.java,
                DATABASE_NAME
            ).build()
        }
    }
}