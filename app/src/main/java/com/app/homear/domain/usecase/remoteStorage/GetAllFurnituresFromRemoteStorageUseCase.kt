package com.app.homear.domain.usecase.remoteStorage

import com.app.homear.domain.model.FileDriveModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.RemoteStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetAllFurnituresFromRemoteStorageUseCase @Inject constructor(
    private val repository: RemoteStorageRepository
){
    operator fun invoke(): Flow<Resource<List<FileDriveModel>>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.getAllFiles()
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}