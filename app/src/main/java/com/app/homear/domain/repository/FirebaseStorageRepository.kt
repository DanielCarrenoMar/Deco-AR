package com.app.homear.domain.repository

import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.ProviderModel
import com.app.homear.domain.model.PublicationModel
import com.app.homear.domain.model.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser

interface FirebaseStorageRepository {
    suspend fun getCollectionModel(): List<FurnitureModel>
    suspend fun getCollectionModelByProvider(provider: String): List<FurnitureModel>
    suspend fun updateUser(id : String,user: UserModel): Boolean
    suspend fun updateUserByKey( key: String, value: String): Boolean
    suspend fun signIn(email: String, password: String): Task<AuthResult>
    suspend fun signUp(email: String, password: String): Task<AuthResult>
    suspend fun currentUser(): FirebaseUser?
    suspend fun isLoggedIn(): Boolean
    suspend fun signOut(): Boolean
    suspend fun updateProvider(provider: ProviderModel): Boolean
    suspend fun updateProvider(key: String, value: String): Boolean
    suspend fun updatePassword(password: String): Boolean
    suspend fun getUser(): UserModel?
    suspend fun getProvider(): ProviderModel?
    suspend fun getFurniture(id: String): FurnitureModel?
    suspend fun getProvider(id: String): ProviderModel?
    suspend fun getUser(id: String): UserModel?
    suspend fun addFurniture(furniture: FurnitureModel): Boolean
    suspend fun deleteFurniture(id: String): Boolean
    suspend fun updateFurniture(furniture: FurnitureModel): Boolean
    suspend fun addProvider(provider: ProviderModel): Boolean
    suspend fun addUser(user: UserModel): Boolean
    suspend fun deleteProvider(id: String): Boolean
    suspend fun deleteUser(id: String): Boolean
    suspend fun getProviderByEmail(email: String): ProviderModel?
    suspend fun getUserByEmail(email: String): UserModel?
    suspend fun getFurnitureByProvider(provider: String): List<FurnitureModel>
    suspend fun addPublication(publication: PublicationModel): Boolean
    suspend fun getPublications(): List<PublicationModel>
    suspend fun getPublication(id: String): PublicationModel?
    suspend fun deletePublication(id: String): Boolean
    suspend fun updatePublication(publication: PublicationModel): Boolean
}