package com.app.homear.domain.usecase.space

import android.util.Log
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.ProjectModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.model.SpaceModel
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class SaveSpaceUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(spaceModel: SpaceModel): Flow<Resource<Long>> = channelFlow {
        try {
            send(Resource.Loading())
            val data = repository.saveSpace(spaceModel)
            if (data.toInt() != -1){
                send(
                    Resource.Success(data = data)
                )
                Log.i("SaveSpaceUseCase", "space guardado")
            } else {
                send(
                    Resource.Error("Save space Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}