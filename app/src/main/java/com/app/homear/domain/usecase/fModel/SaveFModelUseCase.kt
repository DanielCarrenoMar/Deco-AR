package com.app.homear.domain.usecase.fModel

import com.app.homear.domain.model.FModelModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class SaveFModelUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(fModelModel: FModelModel): Flow<Resource<Long>> = channelFlow {
        try {
            send(Resource.Loading())
            val data = repository.saveFModel(fModelModel)
            if (data.toInt() != -1){
                send(
                    Resource.Success(data = data)
                )
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