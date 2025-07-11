package com.app.homear.data.database.dao

import androidx.room.*
import com.app.homear.data.database.entity.SpaceFurnitureEntity

@Dao
interface SpaceFurnitureDao {

    @Query("SELECT * FROM space_furniture")
    suspend fun getAllLocalFurniture(): List<SpaceFurnitureEntity>

    @Query("SELECT * FROM space_furniture WHERE id = :id")
    suspend fun getLocalFurnitureById(id: Int): SpaceFurnitureEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalFurniture(furniture: SpaceFurnitureEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalFurnitureList(furnitureList: List<SpaceFurnitureEntity>)

    @Update
    suspend fun updateLocalFurniture(furniture: SpaceFurnitureEntity): Int

    @Delete
    suspend fun deleteLocalFurniture(furniture: SpaceFurnitureEntity): Int

    @Query("DELETE FROM space_furniture WHERE id = :id")
    suspend fun deleteLocalFurnitureById(id: Int): Int

    @Query("DELETE FROM space_furniture")
    suspend fun deleteAllLocalFurniture(): Int
}