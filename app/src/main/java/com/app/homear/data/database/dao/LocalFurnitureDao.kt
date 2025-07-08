package com.app.homear.data.database.dao

import androidx.room.*
import com.app.homear.data.database.entity.LocalFurnitureEntity

@Dao
interface LocalFurnitureDao {

    @Query("SELECT * FROM local_furniture")
    suspend fun getAllLocalFurniture(): List<LocalFurnitureEntity>

    @Query("SELECT * FROM local_furniture WHERE id = :id")
    suspend fun getLocalFurnitureById(id: Int): LocalFurnitureEntity?

    @Query("SELECT * FROM local_furniture WHERE isDownloaded = 1")
    suspend fun getDownloadedFurniture(): List<LocalFurnitureEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalFurniture(furniture: LocalFurnitureEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocalFurnitureList(furnitureList: List<LocalFurnitureEntity>)

    @Update
    suspend fun updateLocalFurniture(furniture: LocalFurnitureEntity): Int

    @Delete
    suspend fun deleteLocalFurniture(furniture: LocalFurnitureEntity): Int

    @Query("DELETE FROM local_furniture WHERE id = :id")
    suspend fun deleteLocalFurnitureById(id: Int): Int

    @Query("DELETE FROM local_furniture")
    suspend fun deleteAllLocalFurniture(): Int

    @Query("UPDATE local_furniture SET isDownloaded = :isDownloaded WHERE id = :id")
    suspend fun updateDownloadStatus(id: Int, isDownloaded: Boolean): Int
}