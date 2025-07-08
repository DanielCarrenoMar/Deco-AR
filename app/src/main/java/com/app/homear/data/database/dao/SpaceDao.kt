package com.app.homear.data.database.dao

import androidx.room.*
import com.app.homear.data.database.entity.SpaceEntity

@Dao
interface SpaceDao {

    @Query("SELECT * FROM spaces")
    suspend fun getAllSpaces(): List<SpaceEntity>

    @Query("SELECT * FROM spaces WHERE id = :id")
    suspend fun getSpaceById(id: Int): SpaceEntity?

    @Query("SELECT * FROM spaces WHERE idUser = :userId")
    suspend fun getSpacesByUserId(userId: String): List<SpaceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpace(space: SpaceEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpaceList(spaceList: List<SpaceEntity>)

    @Update
    suspend fun updateSpace(space: SpaceEntity): Int

    @Delete
    suspend fun deleteSpace(space: SpaceEntity): Int

    @Query("DELETE FROM spaces WHERE id = :id")
    suspend fun deleteSpaceById(id: Int): Int

    @Query("DELETE FROM spaces WHERE idUser = :userId")
    suspend fun deleteSpacesByUserId(userId: String): Int

    @Query("DELETE FROM spaces")
    suspend fun deleteAllSpaces(): Int

    @Query("UPDATE spaces SET name = :name, description = :description, lastModified = :lastModified WHERE id = :id")
    suspend fun updateSpaceDetails(
        id: Int,
        name: String,
        description: String,
        lastModified: String
    ): Int
}