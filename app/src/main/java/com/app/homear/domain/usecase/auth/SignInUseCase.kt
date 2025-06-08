package com.app.homear.domain.usecase.auth

import com.app.homear.domain.model.Resource
import com.app.homear.domain.repository.LocalStorageRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val repository: LocalStorageRepository
) {
    operator fun invoke(email: String, password: String): Flow<Resource<FirebaseUser?>> = channelFlow {
        send(Resource.Loading())
        try {
            val authResult = repository.signIn(email, password).await()
            val currentUser = authResult.user
            send(Resource.Success(data = currentUser))
        } catch (exception: Exception) {
            send(Resource.Error(exception.message ?: "Unknown Error"))
        }
    }
}