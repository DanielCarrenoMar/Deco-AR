package com.app.homear.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.homear.data.database.dao.FurnitureDao
import com.app.homear.data.database.entity.FurnitureEntity

@Database(entities = [FurnitureEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getFurnitureDao(): FurnitureDao
}