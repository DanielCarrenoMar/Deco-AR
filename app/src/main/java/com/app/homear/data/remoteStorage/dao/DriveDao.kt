package com.app.homear.data.remoteStorage.dao

import android.util.Log
import com.app.homear.data.remoteStorage.DriveApiService
import com.app.homear.domain.model.DriveFileModel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response

open class DriveDao (
    private val driveApiService: DriveApiService,
    protected open val folderId: String
) {
    suspend fun getAllFiles(): List<DriveFileModel> {
        val query = "'$folderId' in parents"
        val response = driveApiService.queryFiles(query)
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
            Log.e("DRIVE","Error al mover al obtener files de capeta de id $folderId $errorMsg")
        }
        return response.body()?.files ?: emptyList()
    }

    suspend fun getFileById(fileId: String): DriveFileModel? {
        val response = driveApiService.getFileById(fileId)
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
            Log.e("DRIVE","Error al mover al obtener file de id $fileId $errorMsg")
            return null
        }
        return response.body()
    }

    suspend fun uploadFile(
        fileName: String,
        fileBytes: ByteArray,
        mimeType: String = "application/octet-stream"
    ): Response<DriveFileModel> {
        val metadataJson = """
            {\n  \"name\": \"$fileName\",\n  \"parents\": [\"$folderId\"]\n}"""
        val metadata = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), metadataJson)
        val fileBody = RequestBody.create(mimeType.toMediaTypeOrNull(), fileBytes)
        val filePart = MultipartBody.Part.createFormData("file", fileName, fileBody)
        return driveApiService.uploadFile(metadata, filePart)
    }

    suspend fun downloadFile(fileId: String) = driveApiService.downloadFile(fileId)

    suspend fun moveFileToTrash(fileId: String): Boolean {
        val response = driveApiService.moveFileToTrash(fileId)
        if (!response.isSuccessful) {
            val errorMsg = response.errorBody()?.string() ?: "Error desconocido"
            Log.e("DRIVE","Error al mover a la papelera: $errorMsg")
        }
        return response.isSuccessful
    }
}
