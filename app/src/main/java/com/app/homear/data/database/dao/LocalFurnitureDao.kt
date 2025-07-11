package com.app.homear.data.database.dao

import androidx.room.*
import com.app.homear.data.database.entity.SpaceFurnitureEntity

@Dao
interface LocalFurnitureDao {

    @Query("SELECT * FROM local_furniture")
    suspend fun getAllLocalFurniture(): List<SpaceFurnitureEntity>

    @Query("SELECT * FROM local_furniture WHERE id = :id")
    suspend fun getLocalFurnitureById(id: Int): SpaceFurnitureEntity?

    @Query("SELECT * FROM local_furniture WHERE isDownloaded = 1")
    suspend fun getDownloadedFurniture(): List<SpaceFurnitureEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalFurniture(furniture: SpaceFurnitureEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalFurnitureList(furnitureList: List<SpaceFurnitureEntity>)

    @Update
    suspend fun updateLocalFurniture(furniture: SpaceFurnitureEntity): Int

    @Delete
    suspend fun deleteLocalFurniture(furniture: SpaceFurnitureEntity): Int

    @Query("DELETE FROM local_furniture WHERE id = :id")
    suspend fun deleteLocalFurnitureById(id: Int): Int

    @Query("DELETE FROM local_furniture")
    suspend fun deleteAllLocalFurniture(): Int

    @Query("UPDATE local_furniture SET isDownloaded = :isDownloaded WHERE id = :id")
    suspend fun updateDownloadStatus(id: Int, isDownloaded: Boolean): Int
}