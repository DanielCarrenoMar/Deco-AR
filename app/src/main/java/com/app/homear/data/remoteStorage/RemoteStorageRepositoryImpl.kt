package com.app.homear.data.remoteStorage

import com.app.homear.data.remoteStorage.dao.FurnitureDriveDao
import com.app.homear.data.remoteStorage.dao.ImageDriveDao
import com.app.homear.domain.model.DriveFileModel
import com.app.homear.domain.repository.RemoteStorageRepository
import com.google.api.services.drive.model.File
import javax.inject.Inject

class RemoteStorageRepositoryImpl @Inject constructor(
    private val furnitureDriveDao: FurnitureDriveDao,
    private val imageDriveDao: ImageDriveDao
) : RemoteStorageRepository {
    override suspend fun getFileById(fileId: String): DriveFileModel? {
        try {
            val response = furnitureDriveDao.getFileById(fileId)
            return response
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun downloadFileById(fileId: String): ByteArray {
        try {
            val responseBody = furnitureDriveDao.downloadFile(fileId)
            val bytes = responseBody.bytes()
            return bytes
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteFileById(fileId: String): Boolean {
        try {
            return furnitureDriveDao.moveFileToTrash(fileId)
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllFurnituresFiles(): List<DriveFileModel> {
        try {
            val response = furnitureDriveDao.getAllFiles()
            return response
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getFurnitureFileByName(fileName: String): DriveFileModel? {
        throw NotImplementedError("uploadFurnitureFile no implementado")
    }

    override suspend fun uploadFurnitureFile(fileName: String, fileBytes: ByteArray): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteFurnitureFile(fileId: String): Boolean {
        throw NotImplementedError("deleteFurnitureFile no implementado")
    }

    override suspend fun downloadFurnitureFile(fileId: String): ByteArray {
        throw NotImplementedError("downloadFurnitureFile no implementado")
    }

    override suspend fun getAllImagesFiles(): List<DriveFileModel> {
        try {
            val response = imageDriveDao.getAllFiles()
            return response
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getImageFileByName(fileName: String): DriveFileModel? {
        throw NotImplementedError("getImageFileByName no implementado")
    }

    override suspend fun uploadImageFile(fileName: String, fileBytes: ByteArray): String {
        TODO("Not yet implemented")
    }

    override suspend fun deleteImageFile(fileId: String): Boolean {
        throw NotImplementedError("deleteImageFile no implementado")
    }

    override suspend fun downloadImageFile(fileId: String): ByteArray {
        throw NotImplementedError("downloadImageFile no implementado")
    }
}
