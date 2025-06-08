package com.app.homear.domain.usecase.auth

import com.app.homear.data.database.repository.LocalStorageRepositoryImpl
import com.app.homear.domain.repository.LocalStorageRepository
import com.google.firebase.auth.FirebaseUser
import javax.inject.Inject

class CurrentUserUseCase @Inject constructor(
    private val repository: LocalStorageRepository
){
    suspend operator fun invoke(): FirebaseUser? {
        try {
            val currentUser = repository.currentUser()
            return currentUser
        } catch (e: Exception) {
            throw e
        }

    }
}