package com.app.homear.data.remoteStorage

import com.app.homear.domain.model.DriveFileModel
import retrofit2.http.GET
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
}
