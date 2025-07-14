package com.app.homear.domain.usecase.firestore

import com.app.homear.domain.model.Resource
import com.app.homear.domain.model.UserModel
import com.app.homear.domain.repository.FirebaseStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class GetCurrentUserUseCase @Inject constructor(
    private val firestoreRepository: FirebaseStorageRepository
){
    operator fun invoke() : Flow<Resource<UserModel>> = channelFlow {
        send(Resource.Loading())
        try {
            val user = firestoreRepository.getUser()
            send(Resource.Success(user))
        } catch (e: Exception) {
            send(Resource.Error(e.message ?: "An unexpected error occurred"))

        }
    }
}