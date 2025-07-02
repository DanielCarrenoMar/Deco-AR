package com.app.homear.domain.usecase.remoteStorage

import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.RemoteStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class DownloadFileFromRemoteByIdUseCase @Inject constructor(
    private val repository: RemoteStorageRepository
){
    operator fun invoke(fileId: String, savePath: String): Flow<Resource<File>> = channelFlow {
        try {
            send(Resource.Loading())
            val byteArray = repository.downloadFileById(fileId)
            val file = File(savePath)
            FileOutputStream(file).use { it.write(byteArray) }
            send(Resource.Success(data = file))
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}