package com.app.homear.domain.usecase.fModel

import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class UpdateFurnitureUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(fModelId: Int, name: String, description: String): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            if (repository.updateFurnitureById(fModelId, name, description)){
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