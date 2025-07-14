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

class DeleteFileFromRemoteByIdUseCase @Inject constructor(
    private val repository: RemoteStorageRepository
){
    operator fun invoke(): Flow<Resource<Boolean>> = channelFlow {
        try {
            send(Resource.Loading())
            send(Resource.Success(data = true))
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "Unknown Error"))
        }
    }
}