package com.app.homear.domain.repository

import com.app.homear.domain.model.FModelModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import java.io.File

interface LocalStorageRepository {
    suspend fun signIn(email: String, password: String): Task<AuthResult>
    suspend fun signUp(email: String, password: String): Task<AuthResult>
    suspend fun currentUser(): FirebaseUser?
    /**
     * Obtiene la direccion de todos los archivos de un directorio
     */
    suspend fun getAllFilesTypeFromDir(directory: String, type: String): List<File>

    suspend fun getAllFModels(): List<FModelModel>
    suspend fun getFModelById(fModelId: Int): FModelModel?
    suspend fun saveFModel(fModelModel: FModelModel): Long
    suspend fun updateFModelById(fModelId: Int, name: String, description: String): Boolean
    suspend fun deleteAllFModels(): Int
    suspend fun deleteFModelFromId(fModelId: Int): Boolean
}