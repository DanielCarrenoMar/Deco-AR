package com.app.homear.data.database.repository


import com.app.homear.data.database.dao.FurnitureDao
import com.app.homear.data.database.dao.SpaceDao
import com.app.homear.data.database.entity.toFurnitureModel
import com.app.homear.data.database.entity.toSpaceModel
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.SpaceModel

import com.app.homear.domain.model.toFurnitureEntity
import com.app.homear.domain.repository.LocalStorageRepository

import java.io.File
import javax.inject.Inject

class LocalStorageRepositoryImpl @Inject constructor(
    private val furnitureDao: FurnitureDao,
    private val spaceDao: SpaceDao
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

    override suspend fun getAllSpaces(): List<SpaceModel> {
        try {
            return spaceDao.getAllSpaces().map { spaceEntity -> spaceEntity.toSpaceModel() }
        } catch (e: Exception) {
            throw e
        }
    }

}