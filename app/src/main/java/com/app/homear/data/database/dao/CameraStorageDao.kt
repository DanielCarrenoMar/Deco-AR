package com.app.homear.data.database.dao

import androidx.room.*
import com.app.homear.data.database.entity.CameraStorageEntity

@Dao
interface CameraStorageDao {

    @Query("SELECT * FROM camera_storage")
    suspend fun getAllCameraStorage(): List<CameraStorageEntity>

    @Query("SELECT * FROM camera_storage WHERE id = :id")
    suspend fun getCameraStorageById(id: Int): CameraStorageEntity?

    @Query("SELECT * FROM camera_storage WHERE furnitureId = :furnitureId")
    suspend fun getCameraStorageByFurnitureId(furnitureId: Int): List<CameraStorageEntity>

    @Query("SELECT * FROM camera_storage WHERE name LIKE '%' || :name || '%'")
    suspend fun searchCameraStorageByName(name: String): List<CameraStorageEntity>

    @Query("SELECT * FROM camera_storage WHERE material LIKE '%' || :material || '%'")
    suspend fun searchCameraStorageByMaterial(material: String): List<CameraStorageEntity>

    @Query("SELECT * FROM camera_storage WHERE keywords LIKE '%' || :keyword || '%'")
    suspend fun searchCameraStorageByKeyword(keyword: String): List<CameraStorageEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCameraStorage(cameraStorage: CameraStorageEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCameraStorageList(cameraStorageList: List<CameraStorageEntity>)

    @Update
    suspend fun updateCameraStorage(cameraStorage: CameraStorageEntity): Int

    @Delete
    suspend fun deleteCameraStorage(cameraStorage: CameraStorageEntity): Int

    @Query("DELETE FROM camera_storage WHERE id = :id")
    suspend fun deleteCameraStorageById(id: Int): Int

    @Query("DELETE FROM camera_storage WHERE furnitureId = :furnitureId")
    suspend fun deleteCameraStorageByFurnitureId(furnitureId: Int): Int

    @Query("DELETE FROM camera_storage")
    suspend fun deleteAllCameraStorage(): Int

    @Query("UPDATE camera_storage SET name = :name, description = :description WHERE id = :id")
    suspend fun updateCameraStorageDetails(id: Int, name: String, description: String): Int

    @Query("UPDATE camera_storage SET modelPath = :modelPath, imagePath = :imagePath WHERE id = :id")
    suspend fun updateCameraStoragePaths(id: Int, modelPath: String, imagePath: String): Int

    @Query("UPDATE camera_storage SET height = :height, width = :width, length = :length WHERE id = :id")
    suspend fun updateCameraStorageDimensions(
        id: Int,
        height: Float,
        width: Float,
        length: Float
    ): Int
}