package com.app.homear.domain.repository

import com.app.homear.data.database.entity.FurnitureEntity
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.ProjectModel
import com.app.homear.domain.model.SpaceModel
import com.app.homear.domain.model.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import java.io.File

interface LocalStorageRepository {

    /**
     * Obtiene la direccion de todos los archivos de un directorio
     */
    suspend fun getAllFilesTypeFromDir(directory: String, type: String): List<File>

    suspend fun getAllFurnitures(): List<FurnitureModel>
    suspend fun getFurnitureById(fModelId: Int): FurnitureModel?
    suspend fun saveFurniture(furnitureModel: FurnitureModel): Long
    suspend fun updateFurnitureById(fModelId: Int, name: String, description: String): Boolean
    suspend fun deleteAllFurnitures(): Int
    suspend fun deleteFurnitureFromId(fModelId: Int): Boolean

    suspend fun getAllProjects(): List<ProjectModel>

    suspend fun getAllSpaces(): List<SpaceModel>

}