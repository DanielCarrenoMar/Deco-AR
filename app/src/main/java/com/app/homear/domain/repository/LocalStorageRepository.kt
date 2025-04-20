package com.app.homear.domain.repository

import com.app.homear.domain.model.FModelModel
import java.io.File

interface LocalStorageRepository {
    /**
     * Obtiene la direccion de todos los archivos de un directorio
     */
    suspend fun getAllFilesTypeFromDir(directory: String, type: String): List<File>

    suspend fun getAllFModels(): List<FModelModel>
    suspend fun insertFModel(fModel: FModelModel): Long
    suspend fun updateFModelById(fModelId: Int, name: String, description: String): Int
    suspend fun deleteAllFModels(): Int
    suspend fun deleteFModelFromId(fModelId: Int): Int
}