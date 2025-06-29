package com.app.homear.data.remote

import com.app.homear.domain.model.FileDriveModel
import retrofit2.http.GET
import retrofit2.http.Query

// Modelos de datos compatibles con Gson

data class DriveFilesResponse(
    val files: List<FileDriveModel>
)

interface DriveApiService {
    @GET("drive/v3/files")
    suspend fun listFiles(
        @Query("key") apiKey: String,
        @Query("q") query: String
    ): DriveFilesResponse
}
