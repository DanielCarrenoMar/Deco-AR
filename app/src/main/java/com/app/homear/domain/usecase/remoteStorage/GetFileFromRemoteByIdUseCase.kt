package com.app.homear.domain.usecase.remoteStorage

import com.app.homear.domain.model.DriveFileModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.RemoteStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetFileFromRemoteByIdUseCase @Inject constructor(
    private val repository: RemoteStorageRepository
){
    operator fun invoke(fileId: String): Flow<Resource<DriveFileModel>> = channelFlow {
        try {
            send(Resource.Loading())
            val responsive = repository.getFileById(fileId)
                ?: throw Exception("File not found")
            send(
                Resource.Success(
                    data = responsive
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}