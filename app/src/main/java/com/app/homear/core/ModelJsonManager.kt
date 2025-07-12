package com.app.homear.core

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream

object ModelJsonManager {
    private const val FILE_NAME = "model.json"

    /**
     * Copia el model.json de assets a internal storage la primera vez.
     */
    fun copyModelJsonIfNeeded(context: Context) {
        val file = File(context.filesDir, FILE_NAME)
        if (!file.exists()) {
            val assetManager = context.assets
            assetManager.open(FILE_NAME).use { inputStream ->
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }
    }

    /**
     * Lee el model.json desde internal storage y retorna el contenido como JSONArray.
     */
    fun readModelJson(context: Context): JSONArray {
        val file = File(context.filesDir, FILE_NAME)
        val content = file.readText()
        return JSONArray(content)
    }

    /**
     * Agrega un nuevo mueble (como JSONObject) al model.json y guarda los cambios.
     */
    fun addFurniture(context: Context, newFurniture: JSONObject) {
        val file = File(context.filesDir, FILE_NAME)
        val json = readModelJson(context)
        json.put(newFurniture)
        file.writeText(json.toString(4)) // pretty print
    }

    /**
     * Si necesitas otros métodos para actualizar, buscar, etc., agrégalos aquí.
     */
}
