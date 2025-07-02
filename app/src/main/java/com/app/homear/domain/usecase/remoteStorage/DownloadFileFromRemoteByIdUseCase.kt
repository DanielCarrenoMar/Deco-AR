package com.app.homear.domain.usecase.remoteStorage

import android.util.Log
import com.app.homear.domain.model.DriveFileModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.RemoteStorageRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flowOn
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class DownloadFileFromRemoteByIdUseCase @Inject constructor(
    private val repository: RemoteStorageRepository
){
    operator fun invoke(fileId: String, savePath: File): Flow<Resource<File>> = channelFlow {
        try {
            send(Resource.Loading())

            val driveFile: DriveFileModel = repository.getFileById(fileId)
                ?: throw Exception("File not found with ID: $fileId")

            Log.i("DRIVE", "Downloading file: ${driveFile.name} with ID: $fileId")

            val byteArray = repository.downloadFileById(fileId)
            val file = File(savePath, driveFile.name)
            FileOutputStream(file).use { it.write(byteArray) }

            send(Resource.Success(data = file))
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}