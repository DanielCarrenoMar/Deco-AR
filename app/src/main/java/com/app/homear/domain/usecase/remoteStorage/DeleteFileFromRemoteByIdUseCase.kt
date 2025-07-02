package com.app.homear.domain.usecase.remoteStorage

import com.app.homear.domain.model.DriveFileModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.RemoteStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class DeleteFileFromRemoteByIdUseCase @Inject constructor(
    private val repository: RemoteStorageRepository
){
    operator fun invoke(fileId: String): Flow<Resource<Boolean>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.deleteFileById(fileId)
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}