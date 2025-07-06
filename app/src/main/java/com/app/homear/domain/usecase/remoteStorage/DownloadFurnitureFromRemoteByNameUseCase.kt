package com.app.homear.domain.usecase.remoteStorage

import android.content.Context
import android.os.Environment.DIRECTORY_PICTURES
import android.util.Log
import com.app.homear.domain.model.DriveFileModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.RemoteStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class DownloadFurnitureFromRemoteByNameUseCase @Inject constructor(
    private val repository: RemoteStorageRepository
){
    operator fun invoke(fileName: String, context: Context): Flow<Resource<File>> = channelFlow {
        try {
            send(Resource.Loading())

            val driveFile: DriveFileModel = repository.getFurnitureFileByName(fileName)
                ?: throw Exception("File not found with name: $fileName")

            val byteArray = repository.downloadFileById(driveFile.id)
            val path = context.getExternalFilesDir(DIRECTORY_PICTURES)
            val file = File(path, driveFile.name)

            FileOutputStream(file).use { it.write(byteArray) }

            send(Resource.Success(data = file))
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}