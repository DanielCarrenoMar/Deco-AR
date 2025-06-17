package com.app.homear.domain.usecase.fModel

import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class DeleteFurnitureFromIdUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(fModelId:Int): Flow<Resource<Unit>> = channelFlow {
        try {
            send(Resource.Loading())
            if (repository.deleteFurnitureFromId(fModelId)){
                send(
                    Resource.Success(Unit)
                )
            }else{
                send(
                    Resource.Error("Delete fModel id: $fModelId Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}