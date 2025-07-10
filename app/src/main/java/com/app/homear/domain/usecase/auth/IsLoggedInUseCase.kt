package com.app.homear.domain.usecase.auth

import com.app.homear.domain.repository.FirebaseStorageRepository
import javax.inject.Inject

class IsLoggedInUseCase @Inject constructor(
    private val authRepository: FirebaseStorageRepository
){
    suspend operator fun invoke(): Boolean {
        try {
            return authRepository.isLoggedIn()
        }catch (e: Exception) {
            return false
        }

    }
}