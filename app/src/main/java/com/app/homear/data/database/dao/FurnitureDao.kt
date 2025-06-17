package com.app.homear.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.homear.data.database.entity.FurnitureEntity

@Dao
interface FurnitureDao {

    @Query("SELECT * FROM furniture")
    suspend fun getAllFurnitures(): List<FurnitureEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFurniture(fModel: FurnitureEntity): Long

    @Query("UPDATE furniture SET name = :name, description = :description WHERE id = :fModelId")
    suspend fun updateFurnitureById(fModelId: Int, name: String, description: String): Int

    @Query("DELETE FROM furniture")
    suspend fun deleteAllFurnitures(): Int

    @Query("DELETE FROM sqlite_sequence WHERE name = 'furniture'")
    suspend fun resetIncremetalFurniture()

    @Query("DELETE FROM furniture WHERE id = :fModelId")
    suspend fun deleteFurnitureFromId(fModelId: Int): Int

    @Query("SELECT * FROM furniture WHERE id = :fModelId")
    suspend fun getFurnitureFromId(fModelId: Int): FurnitureEntity?
}