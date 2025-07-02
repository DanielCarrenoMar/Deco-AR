package com.app.homear.data.remoteStorage.dao

import com.app.homear.data.remoteStorage.DriveApiService
import com.app.homear.domain.model.DriveFileModel
import javax.inject.Inject

class ImageDriveDao @Inject constructor(
    private val driveApiService: DriveApiService
) {
    private val folderId = "1SMhZxlQAABvWxs2L_DrfqOVyR8w_4X-L"

    suspend fun getAllFiles(): List<DriveFileModel> {
        val query = "'$folderId' in parents"
        return driveApiService.queryFiles(query).files
    }
}

