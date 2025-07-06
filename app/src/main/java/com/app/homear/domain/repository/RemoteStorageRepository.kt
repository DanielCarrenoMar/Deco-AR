package com.app.homear.domain.repository

import com.app.homear.domain.model.DriveFileModel

interface RemoteStorageRepository {
    suspend fun getFileById(fileId: String): DriveFileModel?
    suspend fun downloadFileById(fileId: String): ByteArray
    suspend fun deleteFileById(fileId: String): Boolean

    suspend fun getAllFurnituresFiles(): List<DriveFileModel>
    suspend fun getFurnitureFileByName(fileName: String): DriveFileModel?
    suspend fun uploadFurnitureFile(fileName: String, fileBytes: ByteArray): String
    suspend fun deleteFurnitureFile(fileId: String): Boolean
    suspend fun downloadFurnitureFile(fileId: String): ByteArray

    suspend fun getAllImagesFiles(): List<DriveFileModel>
    suspend fun getImageFileByName(fileName: String): DriveFileModel?
    suspend fun uploadImageFile(fileName: String, fileBytes: ByteArray): String
    suspend fun deleteImageFile(fileId: String): Boolean
    suspend fun downloadImageFile(fileId: String): ByteArray
}