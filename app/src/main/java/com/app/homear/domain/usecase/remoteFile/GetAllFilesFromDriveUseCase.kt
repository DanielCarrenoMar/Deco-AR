package com.app.homear.domain.usecase.remoteFile

import com.app.homear.data.database.repository.LocalStorageRepositoryImpl
import com.app.homear.domain.model.FileDriveModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import com.app.homear.domain.repository.RemoteStorageRepository
import com.google.api.services.drive.model.File
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetAllFilesFromDriveUseCase @Inject constructor(
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