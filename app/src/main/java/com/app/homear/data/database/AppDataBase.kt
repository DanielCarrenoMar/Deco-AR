package com.app.homear.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.app.homear.data.database.dao.FurnitureDao
import com.app.homear.data.database.entity.FurnitureEntity

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.app.homear.data.database.dao.ProjectDao
import com.app.homear.data.database.dao.SpaceDao
import com.app.homear.data.database.dao.SpaceFurnitureDao
import com.app.homear.data.database.entity.ProjectEntity
import com.app.homear.data.database.entity.SpaceEntity
import com.app.homear.data.database.entity.SpaceFurnitureEntity

@Database(entities = [FurnitureEntity::class, SpaceEntity::class, SpaceFurnitureEntity::class, ProjectEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getFurnitureDao(): FurnitureDao
    abstract fun getSpaceDao(): SpaceDao
    abstract fun getProjectDao(): ProjectDao
    abstract fun getSpaceFurnitureDao(): SpaceFurnitureDao
}