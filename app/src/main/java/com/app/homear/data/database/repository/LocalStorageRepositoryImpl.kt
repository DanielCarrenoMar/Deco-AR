package com.app.homear.data.database.repository

import com.app.homear.data.database.dao.FModelDao
import com.app.homear.data.database.entity.toFModelModel
import com.app.homear.domain.model.FModelModel
import com.app.homear.domain.model.toFModelEntity
import com.app.homear.domain.repository.LocalStorageRepository
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import java.io.File
import javax.inject.Inject

class LocalStorageRepositoryImpl @Inject constructor(
    private val fModelDao: FModelDao,
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

    override suspend fun getAllFModels(): List<FModelModel> {
        try {
            return fModelDao.getAllFModels().map { fModelEntity -> fModelEntity.toFModelModel() }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getFModelById(fModelId: Int): FModelModel? {
        try {
            val courseEntity = fModelDao.getFModelFromId(fModelId) ?: return null
            return courseEntity.toFModelModel()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveFModel(fModelModel: FModelModel): Long {
        try {
            return fModelDao.insertFModel(fModelModel.toFModelEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateFModelById(
        fModelId: Int,
        name: String,
        description: String
    ): Boolean {
        try {
            val result = fModelDao.updateFModelById(
                fModelId,
                name,
                description
            )
            return result == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllFModels(): Int {
        try {
            fModelDao.resetIncremetalFModel()
            return fModelDao.deleteAllFModels()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteFModelFromId(fModelId: Int): Boolean {
        try {
            return fModelDao.deleteFModelFromId(fModelId) == 1
        } catch (e: Exception) {
            throw e
        }
    }


}