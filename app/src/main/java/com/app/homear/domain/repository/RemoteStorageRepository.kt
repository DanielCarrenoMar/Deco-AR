package com.app.homear.domain.repository

import com.app.homear.domain.model.DriveFileModel
import com.google.api.services.drive.model.File;

interface RemoteStorageRepository {
    suspend fun getAllFurnituresFiles(): List<DriveFileModel>
    suspend fun getFurnitureFileByName(fileName: String): DriveFileModel?
    suspend fun uploadFurnitureFile(file: File): String
    suspend fun deleteFurnitureFile(fileId: String): Boolean
    suspend fun downloadFurnitureFile(fileId: String): ByteArray

    suspend fun getAllImagesFiles(): List<DriveFileModel>
    suspend fun getImageFileByName(fileName: String): DriveFileModel?
    suspend fun uploadImageFile(file: File): String
    suspend fun deleteImageFile(fileId: String): Boolean
    suspend fun downloadImageFile(fileId: String): ByteArray
}