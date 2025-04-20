package com.app.homear.data.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.app.homear.data.database.entity.FModelEntity

@Dao
interface FModelDao {

    @Query("SELECT * FROM fmodel")
    suspend fun getAllCourses(): List<FModelEntity>
}