package com.app.homear.data.remoteStorage.dao

import com.app.homear.data.remoteStorage.DriveApiService
import javax.inject.Inject

class FurnitureDriveDao @Inject constructor(
    driveApiService: DriveApiService
) : DriveDao(driveApiService, FURNITURE_FOLDER_ID) {
    companion object {
        private const val FURNITURE_FOLDER_ID = "1u9jww2TzTjTH1R3sGvSXfbbriSxjRIXw"
    }
}
