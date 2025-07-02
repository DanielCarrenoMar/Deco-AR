package com.app.homear.data.remoteStorage

import com.app.homear.domain.model.DriveFileModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

// Modelos de datos compatibles con Gson

data class DriveFilesResponse(
    val files: List<DriveFileModel>
)

interface DriveApiService {
    @GET("drive/v3/files")
    suspend fun queryFiles(
        @Query("q") query: String
    ): DriveFilesResponse

    @Multipart
    @POST("upload/drive/v3/files?uploadType=multipart")
    suspend fun uploadFile(
        @Part("metadata") metadata: RequestBody,
        @Part file: MultipartBody.Part
    ): Response<DriveFileModel>
}