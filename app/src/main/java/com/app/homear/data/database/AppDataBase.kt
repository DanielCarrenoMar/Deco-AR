package com.app.homear.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.homear.data.database.dao.FurnitureDao
import com.app.homear.data.database.entity.FurnitureEntity

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.app.homear.data.database.dao.SpaceDao
import com.app.homear.data.database.entity.SpaceEntity

class Converters {
    @TypeConverter
    fun fromIntList(list: List<Int>): String = list.joinToString(",")

    @TypeConverter
    fun toIntList(data: String): List<Int> =
        if (data.isEmpty()) emptyList() else data.split(",").map { it.toInt() }
}

@Database(entities = [FurnitureEntity::class, SpaceEntity::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getFurnitureDao(): FurnitureDao
    abstract fun getSpaceDao(): SpaceDao
}