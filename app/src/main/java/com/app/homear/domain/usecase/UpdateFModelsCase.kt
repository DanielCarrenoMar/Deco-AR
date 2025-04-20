package com.app.homear.domain.usecase

import com.app.homear.domain.model.FModelModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import java.io.File
import javax.inject.Inject

class UpdateFModelsCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(fModelId: Int, name: String, description: String): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            if (repository.updateFModelById(fModelId, name, description)){
                send(
                    Resource.Success(data = Unit)
                )
            } else {
                send(
                    Resource.Error("Update grade Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}