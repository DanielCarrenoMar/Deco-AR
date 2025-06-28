package com.app.homear.domain.usecase.firestore


import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetAllCollectionFurnitureUseCase @Inject constructor(
    private val repository: LocalStorageRepository
){
    operator fun invoke(): Flow<Resource<List<FurnitureModel>>> = channelFlow {
        send(Resource.Loading())
        try {
            val furnitureModels = repository.getCollectionModel()
            send(Resource.Success(data = furnitureModels))
        } catch (exception: Exception) {
            send(Resource.Error(exception.message ?: "Unknown Error"))
        }
    }
}