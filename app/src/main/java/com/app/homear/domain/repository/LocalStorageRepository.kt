package com.app.homear.domain.repository

import java.io.File

interface LocalStorageRepository {
    /**
     * Obtiene la direccion de todos los archivos de un directorio
     */
    suspend fun getAllFilesTypeFromDir(directory: String, type: String): List<File>
}