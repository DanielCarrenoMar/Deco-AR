package com.app.homear.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.app.homear.data.database.entity.FModelEntity

@Dao
interface FModelDao {

    @Query("SELECT * FROM fmodel")
    suspend fun getAllFModels(): List<FModelEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFModel(fModel: FModelEntity): Long

    @Query("UPDATE fmodel SET name = :name, description = :description WHERE id = :fModelId")
    suspend fun updateFModelById(fModelId: Int, name: String, description: String): Int

    @Query("DELETE FROM fmodel")
    suspend fun deleteAllFModels(): Int

    @Query("DELETE FROM sqlite_sequence WHERE name = 'fmodel'")
    suspend fun resetIncremetalFModel()

    @Query("DELETE FROM fmodel WHERE id = :fModelId")
    suspend fun deleteFModelFromId(fModelId: Int): Int

    @Query("SELECT * FROM fmodel WHERE id = :fModelId")
    suspend fun getFModelFromId(fModelId: Int): FModelEntity?
}