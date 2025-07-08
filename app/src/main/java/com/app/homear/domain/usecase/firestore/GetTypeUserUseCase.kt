package com.app.homear.domain.usecase.firestore

import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.FirebaseStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetTypeUserUseCase @Inject constructor(
    private val firestoreRepository: FirebaseStorageRepository){
    operator fun invoke(id: String) : Flow<Resource<String>> = channelFlow {
        send(Resource.Loading())
        try {
                firestoreRepository.getUser(id)?.let { user ->
                    send(Resource.Success(user.type))
                }
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "An unexpected error occurred"))
        }
    }
}