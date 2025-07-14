package com.app.homear.domain.usecase.firestore

import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.FirebaseStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class AddFurnitureUseCase @Inject constructor(
    private val firestoreRepository: FirebaseStorageRepository
){
    operator fun invoke(furniture: FurnitureModel) : Flow<Resource<Boolean>> = channelFlow {
        send(Resource.Loading())
        try {
            send(Resource.Loading())
            firestoreRepository.addFurniture(furniture)
            send(Resource.Success(true))
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}