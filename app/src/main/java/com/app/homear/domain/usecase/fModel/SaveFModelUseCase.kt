package com.app.homear.domain.usecase.fModel

import android.util.Log
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class SaveFurnitureUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(furnitureModel: FurnitureModel): Flow<Resource<Long>> = channelFlow {
        try {
            send(Resource.Loading())
            val data = repository.saveFurniture(furnitureModel)
            if (data.toInt() != -1){
                send(
                    Resource.Success(data = data)
                )
                Log.i("SaveFurnitureUseCase", "model guardado")
            } else {
                send(
                    Resource.Error("Save fModel Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}