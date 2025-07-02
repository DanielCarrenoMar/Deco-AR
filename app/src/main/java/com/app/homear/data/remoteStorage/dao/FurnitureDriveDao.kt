package com.app.homear.data.remoteStorage.dao

import com.app.homear.data.remoteStorage.DriveApiService
import com.app.homear.domain.model.DriveFileModel
import javax.inject.Inject

class FurnitureDriveDao @Inject constructor(
    private val driveApiService: DriveApiService
) {
    private val folderId = "1u9jww2TzTjTH1R3sGvSXfbbriSxjRIXw"

    suspend fun getAllFiles(): List<DriveFileModel> {
        val query = "'$folderId' in parents"
        return driveApiService.queryFiles(query).files
    }
}

