package com.app.homear.data.firebase

import android.util.Log
import com.app.homear.data.database.entity.FurnitureEntity
import com.app.homear.data.database.entity.toFurnitureModel
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.ProviderModel
import com.app.homear.domain.model.UserModel
import com.app.homear.domain.model.Superficie
import com.app.homear.domain.repository.FirebaseStorageRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

import kotlinx.coroutines.tasks.await
import java.io.File
import javax.inject.Inject

// DTO específico para deserialización de Firestore
data class FirestoreFurnitureEntity(
    val name: String = "",
    val description: String = "",
    val material: List<String> = emptyList(), // ArrayList de Firestore
    val keywords: List<String> = emptyList(), // ArrayList de Firestore
    val modelPath: String = "",
    val imagePath: String = "",
    val height: Float = 0f,
    val width: Float = 0f,
    val length: Float = 0f,
    val superficie: String = "TODAS" // String en lugar de enum
) {
    fun toFurnitureModel(): FurnitureModel {
        return FurnitureModel(
            name = this.name,
            description = this.description,
            material = this.material.toHashSet(),
            keywords = this.keywords.toHashSet(),
            modelFile = File(this.modelPath),
            imageFile = File(this.imagePath),
            height = this.height,
            width = this.width,
            length = this.length,
            superficie = try {
                Superficie.valueOf(this.superficie)
            } catch (e: Exception) {
                Superficie.TODAS
            }
        )
    }
}

class FirebaseStorageRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
): FirebaseStorageRepository {

    override suspend fun signIn(email: String, password: String): Task<AuthResult> {
        return auth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun signUp(email: String, password: String): Task<AuthResult> {
        return auth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun currentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun signOut(): Boolean {
        return try {
            auth.signOut()
            true
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error al obtener la colección", e)
            false
        }
    }

    override suspend fun updateProvider(provider: ProviderModel): Boolean {
        try {
            firestore.collection("providers").document(provider.id).set(provider).await()
            return true
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error al obtener la colección", e)
            return false
        }
    }

    override suspend fun updateProvider(key: String, value: String): Boolean {
        try {
            val authUser = currentUser()
            firestore.collection("providers").document(authUser?.uid ?: "").update(key, value).await()
            return true
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error al obtener la colección", e)
            return false
        }
    }

    override suspend fun updatePassword(password: String): Boolean {
        val user = auth.currentUser
        try {
            user?.updatePassword(password)?.await()
            return true
        } catch (e: FirebaseAuthException) {
            Log.e("FIRESTORE", "Error al obtener la colección", e)

            when (e.errorCode) {
                "ERROR_INVALID_CREDENTIAL" -> return false
                "ERROR_WRONG_PASSWORD" -> return false
                "ERROR_USER_MISMATCH" -> return false
                "ERROR_REQUIRES_RECENT_LOGIN" -> {
                    try {
                        signIn(user?.email.toString(), password).await()
                        user?.updatePassword(password)?.await()
                        return true
                    } catch (e: Exception) {
                        Log.e("FIRESTORE", "Error al obtener la colección", e)
                        return false
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error al obtener la colección", e)
            return false
        }
        return false
    }


    override suspend fun getCollectionModel(): List<FurnitureModel> {
        return try {
            val snapshot = firestore.collection("models").get().await()
            snapshot.documents.mapNotNull { document ->
                val entity = document.toObject(FirestoreFurnitureEntity::class.java)
                entity?.toFurnitureModel()
            }
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error al obtener la colección", e)
            emptyList()
        }
    }

    override suspend fun getCollectionModelByProvider(provider: String): List<FurnitureModel> {
        try {
            val snapshot =
                firestore.collection("models").whereEqualTo("storeProvider", provider).get()
                    .await()
            return snapshot.documents.mapNotNull { document ->
                val entity = document.toObject(FirestoreFurnitureEntity::class.java)
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

    override suspend fun updateUserByKey(key: String, value: String): Boolean {
        try {
            val authUser = currentUser()
            firestore.collection("users").document(authUser?.uid ?: "").update(key, value).await()
            return true
        } catch (e: Exception) {
            Log.e("FIRESTORE", "Error al obtener la colección", e)
            return false
        }
    }


}

