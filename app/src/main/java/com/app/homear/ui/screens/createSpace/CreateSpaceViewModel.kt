package com.app.homear.ui.screens.createspace

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import java.io.File
import org.json.JSONObject

data class FurnitureModel(
    val name: String,
    val type: String
)

@HiltViewModel
class CreateSpaceViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _imagePath = mutableStateOf<String?>(null)
    val imagePath: State<String?> = _imagePath

    private val _furnitureList = mutableStateOf<List<Pair<String, String>>>(emptyList())
    val furnitureList: State<List<Pair<String, String>>> = _furnitureList

    init {
        // Al iniciar, buscar la última imagen guardada y su lista de modelos
        _imagePath.value = findLastImage()
        loadModelsFromJson()
    }

    private fun findLastImage(): String? {
        val directory = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "DecorAR")
        } else {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/DecorAR")
        }

        return directory.listFiles()
            ?.filter { it.extension.lowercase() == "jpg" }
            ?.maxByOrNull { it.lastModified() }
            ?.absolutePath
    }

    private fun loadModelsFromJson() {
        try {
            val imageFile = _imagePath.value?.let { File(it) } ?: return
            val timestamp = imageFile.nameWithoutExtension.split("_").lastOrNull()?.toLongOrNull() ?: return
            val directory = imageFile.parentFile
            val jsonFile = File(directory, "models_$timestamp.json")

            if (!jsonFile.exists()) {
                // Si no existe el JSON, usar la lista predeterminada
                _furnitureList.value = listOf(
                    "Silla Moderna" to "Silla",
                    "Mesa de Comedor" to "Mesa",
                    "Lámpara de Pie" to "Lámpara"
                )
                return
            }

            val jsonContent = jsonFile.readText()
            val jsonObject = JSONObject(jsonContent)
            val modelsArray = jsonObject.getJSONArray("models")
            val modelsList = mutableListOf<Pair<String, String>>()

            for (i in 0 until modelsArray.length()) {
                val modelObject = modelsArray.getJSONObject(i)
                val name = modelObject.getString("name")
                val type = modelObject.getString("type")
                modelsList.add(name to type)
            }

            _furnitureList.value = modelsList
            Log.d("CreateSpaceViewModel", "Lista de modelos cargada: ${modelsList.size} elementos")
        } catch (e: Exception) {
            Log.e("CreateSpaceViewModel", "Error al cargar la lista de modelos: ${e.message}")
            // En caso de error, usar la lista predeterminada
            _furnitureList.value = listOf(
                "Silla Moderna" to "Silla",
                "Mesa de Comedor" to "Mesa",
                "Lámpara de Pie" to "Lámpara"
            )
        }
    }
} 