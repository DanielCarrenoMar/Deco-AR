package com.app.homear.data.remote

import com.app.homear.domain.model.FileDriveModel
import com.app.homear.domain.repository.RemoteStorageRepository
import com.google.api.services.drive.model.File
import javax.inject.Inject
import javax.inject.Named

class RemoteStorageRepositoryImpl @Inject constructor(
    private val driveApiService: DriveApiService,
    @Named("driveApiKey") private val apiKey: String
) : RemoteStorageRepository {
    override suspend fun getAllFiles(): List<FileDriveModel>{
        val folderId = "1u9jww2TzTjTH1R3sGvSXfbbriSxjRIXw"
        try {
            val response = driveApiService.listFiles(apiKey, "'$folderId' in parents")
            return response.files
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getFileByName(fileName: String): FileDriveModel? {
        throw NotImplementedError("uploadFile no implementado")
    }

    override suspend fun uploadFile(file: File): String {
        // Implementación pendiente: subir archivo requiere multipart y endpoint específico
        throw NotImplementedError("uploadFile no implementado")
    }

    override suspend fun deleteFile(fileId: String): Boolean {
        // Implementación pendiente: eliminar archivo requiere endpoint específico
        throw NotImplementedError("deleteFile no implementado")
    }

    override suspend fun downloadFile(fileId: String): ByteArray {
        // Implementación pendiente: descargar archivo requiere endpoint específico
        throw NotImplementedError("downloadFile no implementado")
    }
}
