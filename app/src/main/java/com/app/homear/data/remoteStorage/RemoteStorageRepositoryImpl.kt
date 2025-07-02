package com.app.homear.data.remoteStorage

import com.app.homear.domain.model.FileDriveModel
import com.app.homear.domain.repository.RemoteStorageRepository
import com.google.api.services.drive.model.File
import javax.inject.Inject
import javax.inject.Named

class RemoteStorageRepositoryImpl @Inject constructor(
    private val driveApiService: DriveApiService,
    @Named("driveApiKey") private val apiKey: String
) : RemoteStorageRepository {
    private val folderModelsId = "1u9jww2TzTjTH1R3sGvSXfbbriSxjRIXw"
    private val folderImagesId = "1SMhZxlQAABvWxs2L_DrfqOVyR8w_4X-L"

    override suspend fun getAllFurnituresFiles(): List<FileDriveModel> {
        try {
            val response = driveApiService.listFiles(apiKey, "'$folderModelsId' in parents")
            return response.files
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getFurnitureFileByName(fileName: String): FileDriveModel? {
        throw NotImplementedError("getFurnitureFileByName no implementado")
    }

    override suspend fun uploadFurnitureFile(file: File): String {
        throw NotImplementedError("uploadFurnitureFile no implementado")
    }

    override suspend fun deleteFurnitureFile(fileId: String): Boolean {
        throw NotImplementedError("deleteFurnitureFile no implementado")
    }

    override suspend fun downloadFurnitureFile(fileId: String): ByteArray {
        throw NotImplementedError("downloadFurnitureFile no implementado")
    }

    override suspend fun getAllImagesFiles(): List<FileDriveModel> {
        try {
            val response = driveApiService.listFiles(apiKey, "'$folderImagesId' in parents")
            return response.files
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getImageFileByName(fileName: String): FileDriveModel? {
        throw NotImplementedError("getImageFileByName no implementado")
    }

    override suspend fun uploadImageFile(file: File): String {
        throw NotImplementedError("uploadImageFile no implementado")
    }

    override suspend fun deleteImageFile(fileId: String): Boolean {
        throw NotImplementedError("deleteImageFile no implementado")
    }

    override suspend fun downloadImageFile(fileId: String): ByteArray {
        throw NotImplementedError("downloadImageFile no implementado")
    }
}
