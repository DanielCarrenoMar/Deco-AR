package com.app.homear.data.database.repository


import com.app.homear.data.database.dao.FurnitureDao
import com.app.homear.data.database.dao.ProjectDao
import com.app.homear.data.database.dao.SpaceDao
import com.app.homear.data.database.dao.SpaceFurnitureDao
import com.app.homear.data.database.entity.toFurnitureModel
import com.app.homear.data.database.entity.toProjectEntity
import com.app.homear.data.database.entity.toProjectModel
import com.app.homear.data.database.entity.toSpaceEntity
import com.app.homear.data.database.entity.toSpaceFurnitureEntity
import com.app.homear.data.database.entity.toSpaceFurnitureModel
import com.app.homear.data.database.entity.toSpaceModel
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.ProjectModel
import com.app.homear.domain.model.SpaceFurnitureModel
import com.app.homear.domain.model.SpaceModel

import com.app.homear.domain.model.toFurnitureEntity
import com.app.homear.domain.repository.LocalStorageRepository

import java.io.File
import javax.inject.Inject

class LocalStorageRepositoryImpl @Inject constructor(
    private val furnitureDao: FurnitureDao,
    private val projectDao: ProjectDao,
    private val spaceDao: SpaceDao,
    private val spaceFurnitureDao: SpaceFurnitureDao
) : LocalStorageRepository {


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

    override suspend fun getAllProjects(): List<ProjectModel> {
        try {
            return projectDao.getAllProjects().map { spaceEntity -> spaceEntity.toProjectModel() }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getProjectById(projectId: Int): ProjectModel? {
        try {
            val projectEntity = projectDao.getProjectById(projectId) ?: return null
            return projectEntity.toProjectModel()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveProject(projectModel: ProjectModel): Long {
        try {
            return projectDao.insertProject(projectModel.toProjectEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateProjectById(
        projectId: Int,
        name: String,
        description: String
    ): Boolean {
        try {
            val lastModified = System.currentTimeMillis().toString()
            val result = projectDao.updateProjectDetails(
                projectId,
                name,
                description,
                lastModified
            )
            return result == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllProjects(): Int {
        try {
            return projectDao.deleteAllProjects()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteProjectFromId(projectId: Int): Boolean {
        try {
            return projectDao.deleteProjectById(projectId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllSpaces(): List<SpaceModel> {
        try {
            return spaceDao.getAllSpaces().map { spaceEntity -> spaceEntity.toSpaceModel() }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSpacesByProjectId(projectId: Int): List<SpaceModel> {
        try {
            return spaceDao.getAllSpaces().filter { it.projectId == projectId }.map { it.toSpaceModel() }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSpaceById(spaceId: Int): SpaceModel? {
        try {
            val spaceEntity = spaceDao.getSpaceById(spaceId) ?: return null
            return spaceEntity.toSpaceModel()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveSpace(spaceModel: SpaceModel): Long {
        try {
            return spaceDao.insertSpace(spaceModel.toSpaceEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateSpaceById(spaceId: Int, name: String, description: String): Boolean {
        try {
            val lastModified = System.currentTimeMillis().toString()
            val result = spaceDao.updateSpaceDetails(spaceId, name, description, lastModified)
            return result == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllSpaces(): Int {
        try {
            return spaceDao.deleteAllSpaces()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteSpaceFromId(spaceId: Int): Boolean {
        try {
            return spaceDao.deleteSpaceById(spaceId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getAllSpacesFurniture(): List<SpaceFurnitureModel> {
        try {
            return spaceFurnitureDao.getAllLocalFurniture().map { it.toSpaceFurnitureModel() }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSpacesFurnitureBySpaceId(spaceId: Int): List<SpaceFurnitureModel> {
        try {
            return spaceFurnitureDao.getAllLocalFurniture().filter { it.spaceId == spaceId }.map { it.toSpaceFurnitureModel() }
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun getSpaceFurnitureById(spaceFurnitureId: Int): SpaceFurnitureModel? {
        try {
            val entity = spaceFurnitureDao.getLocalFurnitureById(spaceFurnitureId) ?: return null
            return entity.toSpaceFurnitureModel()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun saveSpaceFurniture(spaceFurnitureModel: SpaceFurnitureModel): Long {
        try {
            return spaceFurnitureDao.insertLocalFurniture(spaceFurnitureModel.toSpaceFurnitureEntity())
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun updateSpaceFurnitureById(spaceFurnitureId: Int, name: String, description: String): Boolean {
        try {
            val entity = spaceFurnitureDao.getLocalFurnitureById(spaceFurnitureId) ?: return false
            val updatedEntity = entity.copy(name = name, description = description)
            return spaceFurnitureDao.updateLocalFurniture(updatedEntity) == 1
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteAllSpacesFurniture(): Int {
        try {
            return spaceFurnitureDao.deleteAllLocalFurniture()
        } catch (e: Exception) {
            throw e
        }
    }

    override suspend fun deleteSpaceFurnitureFromId(spaceFurnitureId: Int): Boolean {
        try {
            return spaceFurnitureDao.deleteLocalFurnitureById(spaceFurnitureId) == 1
        } catch (e: Exception) {
            throw e
        }
    }

}