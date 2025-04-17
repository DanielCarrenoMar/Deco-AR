package com.app.homear.domain.usecase

import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File
import javax.inject.Inject

class GetModelFilesFromDirUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(dir:String): Flow<Resource<List<File>>> = channelFlow {
        try {
            send(Resource.Loading())
            send(
                Resource.Success(
                    data = repository.getAllFilesTypeFromDir(dir, ".glb")
                )
            )
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}