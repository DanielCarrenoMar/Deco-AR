package com.app.homear.domain.repository

import com.app.homear.domain.model.FileDriveModel
import com.google.api.services.drive.model.File;

interface RemoteStorageRepository {
    suspend fun getAllFiles(): List<FileDriveModel>
    suspend fun getFileByName(fileName: String): FileDriveModel?
    suspend fun uploadFile(file: File): String
    suspend fun deleteFile(fileId: String): Boolean
    suspend fun downloadFile(fileId: String): ByteArray
}