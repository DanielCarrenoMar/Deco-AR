package com.app.homear.data.database.repository

import android.util.Log
import com.app.homear.domain.repository.LocalStorageRepository
import java.io.File
import javax.inject.Inject

class LocalStorageRepositoryImpl @Inject constructor(

) : LocalStorageRepository {
    override suspend fun getAllFilesTypeFromDir(directory: String, type: String): List<File> {
        val dir = File(directory)
        val files = dir.listFiles()
        return files?.filter {it.isFile && it.name.endsWith(type, ignoreCase = true)} ?: emptyList()
    }
}