package com.app.homear.data.database.repository


import com.app.homear.data.database.dao.FModelDao
import com.app.homear.domain.model.FModelModel
import com.app.homear.domain.model.toFModelEntity
import com.app.homear.domain.repository.LocalStorageRepository
import java.io.File
import javax.inject.Inject

class LocalStorageRepositoryImpl @Inject constructor(
 private val fModelDao: FModelDao
) : LocalStorageRepository {
    override suspend fun getAllFilesTypeFromDir(directory: String, type: String): List<File> {
        val dir = File(directory)
        val files = dir.listFiles()
        return files?.filter {it.isFile && it.name.endsWith(type, ignoreCase = true)} ?: emptyList()
    }

    override suspend fun getAllFModels(): List<FModelModel> {
        try{
           return fModelDao.getAllFModels().map { fModelEntity ->
               FModelModel(
                   name = fModelEntity.name,
                   description = fModelEntity.description,
                   material = stringToHashSet(fModelEntity.material),
                   keywords = stringToHashSet(fModelEntity.keywords),
                   modelPath = File(fModelEntity.modelPath),
                   imagePath = File(fModelEntity.imagePath),
                   height = fModelEntity.heigth,
                   width = fModelEntity.width,
                   length = fModelEntity.length,
               )
           }
        }catch (e: Exception){
            throw e
        }
    }

    override suspend fun insertFModel(fModel: FModelModel): Int {
        try{
            return fModelDao.insertFModel(fModel.toFModelEntity()).toInt()
        }catch (e: Exception){
            throw e
        }
    }

    override suspend fun updateFModelById(fModelId: Int, name: String, description: String): Boolean {
        try{
            return fModelDao.updateFModelById(fModelId,name,description) != -1
        }catch (e: Exception){
            throw e
        }
    }

    override suspend fun deleteAllFModels(): Int {
        try {
            return fModelDao.deleteAllFModels()
        }catch (e: Exception){
            throw e
        }
    }

    override suspend fun deleteFModelFromId(fModelId: Int): Boolean {
        try{
            return fModelDao.deleteFModelFromId(fModelId) != -1
        }catch (e: Exception){
            throw e
        }
    }

    private fun stringToHashSet(input: String): HashSet<String> {
        return input.split(",")
            .map { it.trim() }  // Elimina espacios en blanco alrededor de cada elemento
            .filter { it.isNotEmpty() }  // Filtra elementos vacíos (por si hay ",,")
            .toHashSet()  // Convierte a HashSet (elimina duplicados automáticamente)
    }
}