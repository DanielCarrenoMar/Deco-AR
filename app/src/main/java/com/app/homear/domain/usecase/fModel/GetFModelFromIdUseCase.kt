package com.app.homear.domain.usecase.fModel

import com.app.homear.domain.model.FModelModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetFModelFromIdUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(fModelId: Int): Flow<Resource<FModelModel>> = channelFlow {
        try {
            send(Resource.Loading())
            val fModel = repository.getFModelById(fModelId)
            if (fModel != null){
                send(
                    Resource.Success(fModel)
                )
            }else{
                send(
                    Resource.Error("Get fModel id: $fModelId Error")
                )
            }
        } catch (e: Exception) {
            send(
                Resource.Error(e.message ?: "Unknown Error")
            )
        }
    }
}