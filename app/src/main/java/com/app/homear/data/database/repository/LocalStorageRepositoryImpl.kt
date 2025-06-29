package com.app.homear.data.database.repository

import android.util.Log
import androidx.compose.foundation.layout.add
import com.app.homear.data.database.dao.FurnitureDao
import com.app.homear.data.database.entity.FurnitureEntity
import com.app.homear.data.database.entity.toFurnitureModel
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.UserModel
import com.app.homear.domain.model.toFurnitureEntity
import com.app.homear.domain.repository.LocalStorageRepository
import com.google.android.gms.tasks.Task
import com.google.ar.core.dependencies.e
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

class LocalStorageRepositoryImpl @Inject constructor(
    private val FurnitureDao: FurnitureDao,
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : LocalStorageRepository {

    override suspend fun signIn(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun signUp(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun getCollectionModel(): List<FurnitureModel> {
        return try {
            val snapshot = firestore.collection("models").get().await()
            snapshot.documents.mapNotNull { document ->
                val entity = document.toObject(FurnitureEntity::class.java)
                entity?.toFurnitureModel()
            }
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error al obtener la colección", e)
            emptyList()
        }
    }

    override suspend fun getAllFilesTypeFromDir(directory: String, type: String): List<File> {
        val dir = File(directory)
        val files = dir.listFiles()
        return files?.filter {it.isFile && it.name.endsWith(type, ignoreCase = true)} ?: emptyList()
    }

    override suspend fun getAllFurnitures(): List<FurnitureModel> {
        try {
            return FurnitureDao.getAllFurnitures().map { furnitureEntity -> furnitureEntity.toFurnitureModel() }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getFurnitureById(fModelId: Int): FurnitureModel? {
        try {
            val courseEntity = FurnitureDao.getFurnitureFromId(fModelId) ?: return null
            return courseEntity.toFurnitureModel()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveFurniture(furnitureModel: FurnitureModel): Long {
        try {
            return FurnitureDao.insertFurniture(furnitureModel.toFurnitureEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateFurnitureById(
        fModelId: Int,
        name: String,
        description: String
    ): Boolean {
        try {
            val result = FurnitureDao.updateFurnitureById(
                fModelId,
                name,
                description
            )
            return result == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllFurnitures(): Int {
        try {
            FurnitureDao.resetIncremetalFurniture()
            return FurnitureDao.deleteAllFurnitures()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteFurnitureFromId(fModelId: Int): Boolean {
        try {
            return FurnitureDao.deleteFurnitureFromId(fModelId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getCollectionModelByProvider(provider: String): List<FurnitureModel> {
        try{
            val snapshot = firestore.collection("models").whereEqualTo("storeProvider", provider).get().await()
            return snapshot.documents.mapNotNull { document ->
                val entity = document.toObject(FurnitureEntity::class.java)
                entity?.toFurnitureModel()
            }
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error al obtener la colección", e)
            return emptyList()
        }

    }

    override suspend fun updateUser(user: UserModel): Boolean {
        try {
            val authUser = currentUser()
            firestore.collection("users").document(authUser?.uid ?: "").set(user).await()
            return true
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error al obtener la colección", e)
            return false

        }
    }

}