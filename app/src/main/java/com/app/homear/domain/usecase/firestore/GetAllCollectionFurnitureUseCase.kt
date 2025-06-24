package com.app.homear.domain.usecase.firestore

import com.app.homear.data.database.entity.FurnitureEntity
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetAllCollectionFurnitureUseCase @Inject constructor(
    private val repository: LocalStorageRepository
){
    operator fun invoke(string: String = "furniture"): Flow<Resource<List<FurnitureEntity>>> = channelFlow {
        send(Resource.Loading())
        try {
            val furnitureModels = repository.getCollectionModel(string)
            send(Resource.Success(data = furnitureModels))
        } catch (exception: Exception) {
            send(Resource.Error(exception.message ?: "Unknown Error"))
        }
    }
}