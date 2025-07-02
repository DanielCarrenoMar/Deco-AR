package com.app.homear.data.remoteStorage.dao

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
        return driveApiService.queryFiles(query).files
    }

    suspend fun getFileById(fileId: String): DriveFileModel? {
        val query = "'$folderId' in parents and id = '$fileId'"
        return driveApiService.queryFiles(query).files.firstOrNull()
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
}
