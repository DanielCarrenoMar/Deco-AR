package com.app.homear.domain.usecase.remoteStorage

import com.app.homear.domain.model.DriveFileModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.RemoteStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetFirstFurnitureFromRemoteByNameUseCase @Inject constructor(
    private val repository: RemoteStorageRepository
){
    operator fun invoke(fileName: String): Flow<Resource<DriveFileModel>> = channelFlow {
        try {
            send(Resource.Loading())
            val responsive = repository.getFurnitureFileByName(fileName)
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