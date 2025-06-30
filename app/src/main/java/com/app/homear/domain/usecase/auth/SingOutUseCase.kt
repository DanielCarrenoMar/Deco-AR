package com.app.homear.domain.usecase.auth

import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.FirebaseStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class SingOutUseCase @Inject constructor(
    private val repository: FirebaseStorageRepository
) {
    operator fun invoke(): Flow<Resource<Boolean>> = channelFlow {
        send(Resource.Loading())
        try{
            val result = repository.signOut()
            send(Resource.Success(data = result))
        }catch (exception: Exception) {
            send(Resource.Error(exception.message ?: "Unknown Error"))
        }
    }
}