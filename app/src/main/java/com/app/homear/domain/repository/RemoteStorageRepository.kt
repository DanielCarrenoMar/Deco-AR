package com.app.homear.domain.repository

import com.app.homear.domain.model.FileDriveModel
import com.google.api.services.drive.model.File;

interface RemoteStorageRepository {
    suspend fun getAllFurnituresFiles(): List<FileDriveModel>
    suspend fun getFurnitureFileByName(fileName: String): FileDriveModel?
    suspend fun uploadFurnitureFile(file: File): String
    suspend fun deleteFurnitureFile(fileId: String): Boolean
    suspend fun downloadFurnitureFile(fileId: String): ByteArray

    suspend fun getAllImagesFiles(): List<FileDriveModel>
    suspend fun getImageFileByName(fileName: String): FileDriveModel?
    suspend fun uploadImageFile(file: File): String
    suspend fun deleteImageFile(fileId: String): Boolean
    suspend fun downloadImageFile(fileId: String): ByteArray
}