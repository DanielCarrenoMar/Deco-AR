package com.app.homear.data.database.repository

import com.app.homear.data.database.dao.FurnitureDao
import com.app.homear.data.database.entity.toFurnitureModel
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.toFurnitureEntity
import com.app.homear.domain.repository.LocalStorageRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.File
import javax.inject.Inject

class LocalStorageRepositoryImpl @Inject constructor(
    private val furnitureDao: FurnitureDao,
    private val auth: FirebaseAuth
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

    override suspend fun getAllFilesTypeFromDir(directory: String, type: String): List<File> {
        val dir = File(directory)
        val files = dir.listFiles()
        return files?.filter {it.isFile && it.name.endsWith(type, ignoreCase = true)} ?: emptyList()
    }

    override suspend fun getAllFurnitures(): List<FurnitureModel> {
        try {
            return furnitureDao.getAllFurnitures().map { furnitureEntity -> furnitureEntity.toFurnitureModel() }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getFurnitureById(fModelId: Int): FurnitureModel? {
        try {
            val courseEntity = furnitureDao.getFurnitureFromId(fModelId) ?: return null
            return courseEntity.toFurnitureModel()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveFurniture(furnitureModel: FurnitureModel): Long {
        try {
            return furnitureDao.insertFurniture(furnitureModel.toFurnitureEntity())
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
            val result = furnitureDao.updateFurnitureById(
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
            furnitureDao.resetIncremetalFurniture()
            return furnitureDao.deleteAllFurnitures()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteFurnitureFromId(fModelId: Int): Boolean {
        try {
            return furnitureDao.deleteFurnitureFromId(fModelId) == 1
        } catch (e: Exception) {
            throw e
        }
    }


}