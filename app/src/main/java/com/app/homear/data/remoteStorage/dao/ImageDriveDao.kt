package com.app.homear.data.remoteStorage.dao

import com.app.homear.data.remoteStorage.DriveApiService
import javax.inject.Inject

class ImageDriveDao @Inject constructor(
    driveApiService: DriveApiService
) : DriveDao(driveApiService, IMAGE_FOLDER_ID) {
    companion object {
        private const val IMAGE_FOLDER_ID = "1SMhZxlQAABvWxs2L_DrfqOVyR8w_4X-L"
    }
}
