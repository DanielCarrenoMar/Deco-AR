package com.app.homear.domain.repository

import com.app.homear.data.database.entity.FurnitureEntity
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.ProjectModel
import com.app.homear.domain.model.SpaceFurnitureModel
import com.app.homear.domain.model.SpaceModel
import com.app.homear.domain.model.UserModel
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import java.io.File
import kotlinx.coroutines.flow.Flow
import com.app.homear.domain.model.Resource

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
    suspend fun saveProject(projectModel: ProjectModel): Long
    suspend fun updateProjectById(
        projectId: Int,
        name: String,
        description: String
    ): Boolean
    suspend fun deleteAllProjects(): Int
    suspend fun deleteProjectFromId(projectId: Int): Boolean

    suspend fun getAllSpaces(): List<SpaceModel>
    suspend fun getSpacesByProjectId(projectId: Int): List<SpaceModel>
    suspend fun saveSpace(spaceModel: SpaceModel): Long
    suspend fun updateSpaceById(
        spaceId: Int,
        name: String,
        description: String
    ): Boolean
    suspend fun deleteAllSpaces(): Int
    suspend fun deleteSpaceFromId(spaceId: Int): Boolean

    suspend fun saveSpaceList(spaceList: List<SpaceModel>)

    suspend fun getAllSpacesFurniture(): List<SpaceFurnitureModel>
    suspend fun getSpacesFurnitureBySpaceId(spaceId: Int): List<SpaceFurnitureModel>
    suspend fun getSpaceFurnitureById(spaceFurnitureId: Int): SpaceFurnitureModel?
    suspend fun saveSpaceFurniture(spaceFurnitureModel: SpaceFurnitureModel): Long
    suspend fun updateSpaceFurnitureById(
        spaceFurnitureId: Int,
        name: String,
        description: String
    ): Boolean
    suspend fun deleteAllSpacesFurniture(): Int
    suspend fun deleteSpaceFurnitureFromId(spaceFurnitureId: Int): Boolean

    fun getProjectById(projectId: Int): Flow<Resource<ProjectModel>>
    
    fun getSpaceById(spaceId: Int): Flow<Resource<SpaceModel>>

}