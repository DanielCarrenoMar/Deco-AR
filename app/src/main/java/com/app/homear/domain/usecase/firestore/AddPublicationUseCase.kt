package com.app.homear.domain.usecase.firestore

import com.app.homear.domain.model.PublicationModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.FirebaseStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class AddPublicationUseCase @Inject constructor(
    private val firestoreRepository: FirebaseStorageRepository){
    operator fun invoke(publication: PublicationModel): Flow<Resource<Boolean>> = channelFlow {
        send(Resource.Loading())
        try {
            firestoreRepository.addPublication(publication)
            send(Resource.Success(true))
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}