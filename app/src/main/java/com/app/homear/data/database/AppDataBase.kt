package com.app.homear.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.homear.data.database.dao.FModelDao
import com.app.homear.data.database.entity.FModelEntity

@Database(entities = [FModelEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getFModelDao(): FModelDao
}