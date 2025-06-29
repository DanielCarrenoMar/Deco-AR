package com.app.homear.domain.repository

import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface FirebaseStorageRepository {
    suspend fun getCollectionModel(): List<FurnitureModel>
    suspend fun getCollectionModelByProvider(provider: String): List<FurnitureModel>
    suspend fun updateUser(user: UserModel): Boolean
    suspend fun signIn(email: String, password: String): Task<AuthResult>
    suspend fun signUp(email: String, password: String): Task<AuthResult>
    suspend fun currentUser(): FirebaseUser?
}