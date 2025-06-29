package com.app.homear.domain.usecase.firestore

import com.app.homear.domain.model.Resource
import com.app.homear.domain.model.UserModel
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import javax.inject.Inject

class UpdateUserModelUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(user: UserModel): Flow<Resource<Boolean>> = channelFlow {
        send(Resource.Loading())
        try {
            val result = repository.updateUser(user)
            send(Resource.Success(data = result))
        } catch (exception: Exception) {
            send(Resource.Error(exception.message ?: "Unknown Error"))
        }

    }
}