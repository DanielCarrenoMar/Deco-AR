package com.app.homear.ui.screens.createSpace

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import java.io.File
import org.json.JSONObject
import com.app.homear.core.utils.SharedPreferenceHelper
import com.app.homear.domain.model.Resource
import com.app.homear.domain.model.SpaceFurnitureModel
import com.app.homear.domain.model.SpaceModel
import com.app.homear.domain.usecase.space.SaveSpaceUseCase
import com.app.homear.domain.usecase.spaceFurniture.SaveSpaceFurnitureUseCase
import kotlinx.coroutines.launch
import org.json.JSONArray

data class FurnitureModel(
    val name: String,
    val type: String
)

data class FurniturePreviewModel(
    val name: String,
    val type: String,
    val description: String?,
    val imagePath: String?
)

@HiltViewModel
class CreateSpaceViewModel @Inject constructor(
    private val saveSpaceUseCase: SaveSpaceUseCase,
    private val saveSpaceFurnitureUseCase: SaveSpaceFurnitureUseCase
) : ViewModel() {

    private val _imagePath = mutableStateOf<String?>(null)
    val imagePath = _imagePath

    private val _spaceName = mutableStateOf<String>("")
    val spaceName = _spaceName

    private val _furnitureList = mutableStateOf<List<FurniturePreviewModel>>(emptyList())
    val furnitureList: State<List<FurniturePreviewModel>> = _furnitureList


    fun initContent(context: Context){
        _imagePath.value = findLastImage()
        loadModelsFromPrefsOrJson(context)
    }

    fun saveSpace(){
        viewModelScope.launch {
            val currentDate = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm", java.util.Locale.getDefault()).format(java.util.Date())
            val spaceModel = SpaceModel(
                id = -1, // No hace falta ID al guardar, se genera automáticamente
                projectId = -1,
                idUser = "dafault_user",
                name = _spaceName.value.ifBlank { "Espacio sin nombre" },
                description = "Sin descripcion",
                imagePath = imagePath.value ?: "",
                createdDate = currentDate,
                lastModified = currentDate
            )

            saveSpaceUseCase(spaceModel).collect { resource ->
                when (resource) {
                    is Resource.Loading -> {
                        // Handle loading state if needed
                    }
                    is Resource.Success -> {
                        val idSaved = resource.data!!
                        Log.d("ProjectsViewModel", "Proyecto guardado correctamente con ID: $idSaved")
                        Log.d("ProjectsViewModel", "Guardando muebles con cantidad de: ${_furnitureList.value.size}")
                        for (furniture in _furnitureList.value) {
                            val furnitureModel = SpaceFurnitureModel(
                                id = -1, // No hace falta ID al guardar, se genera automáticamente
                                spaceId = idSaved.toInt(),
                                name = furniture.name,
                                imagePath = furniture.imagePath ?: "",
                                modelPath = furniture.type,
                                description = furniture.description ?: "Sin descripción",
                            )
                            saveSpaceFurnitureUseCase(furnitureModel).collect { furnitureResource ->
                                when (furnitureResource) {
                                    is Resource.Loading -> {
                                        // Handle loading state if needed
                                    }
                                    is Resource.Success -> {
                                        Log.d("ProjectsViewModel", "Proyecto guardado correctamente: ${furnitureModel.name}")
                                    }
                                    is Resource.Error -> {
                                        Log.e("ProjectsViewModel", "Error al guardar el mueble: ${furnitureResource.message}")
                                    }
                                }
                            }
                        }
                    }
                    is Resource.Error -> {
                        Log.e("ProjectsViewModel", "Error al guardar el proyecto: ${resource.message}")
                    }
                }
            }
        }
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

    private fun loadModelsFromJson(context: Context) {
        try {
            val imageFile = _imagePath.value?.let { File(it) } ?: return
            val timestamp = imageFile.nameWithoutExtension.split("_").lastOrNull()?.toLongOrNull() ?: return
            val directory = imageFile.parentFile
            val jsonFile = File(directory, "models_$timestamp.json")

            if (!jsonFile.exists()) {
                // Si no existe el JSON, usar la lista predeterminada
                _furnitureList.value = listOf(
                    FurniturePreviewModel("Silla Moderna", "Silla", null, null),
                    FurniturePreviewModel("Mesa de Comedor", "Mesa", null, null),
                    FurniturePreviewModel("Lámpara de Pie", "Lámpara", null, null)
                )
                return
            }

            val jsonContent = jsonFile.readText()
            val jsonObject = JSONObject(jsonContent)
            val modelsArray = jsonObject.getJSONArray("models")
            val modelsList = mutableListOf<FurniturePreviewModel>()

            for (i in 0 until modelsArray.length()) {
                val modelObject = modelsArray.getJSONObject(i)
                val name = modelObject.getString("name")
                val type = modelObject.getString("type")
                val details = getModelDetailsByName(name, context)
                modelsList.add(
                    FurniturePreviewModel(
                        name = name,
                        type = type,
                        description = details?.first,
                        imagePath = details?.second
                    )
                )
            }

            _furnitureList.value = modelsList
            Log.d("CreateSpaceViewModel", "Lista de modelos cargada: ${modelsList.size} elementos")
        } catch (e: Exception) {
            Log.e("CreateSpaceViewModel", "Error al cargar la lista de modelos: ${e.message}")
            // En caso de error, usar la lista predeterminada
            _furnitureList.value = listOf(
                FurniturePreviewModel("Silla Moderna", "Silla", null, null),
                FurniturePreviewModel("Mesa de Comedor", "Mesa", null, null),
                FurniturePreviewModel("Lámpara de Pie", "Lámpara", null, null)
            )
        }
    }

    /**
     * Busca en models.json el modelo por nombre y retorna description e imagePath absolutos
     */
    private fun getModelDetailsByName(name: String, context: Context): Pair<String?, String?>? {
        try {
            val internalJsonFile = File(context.filesDir, "assets/models.json")
            if (!internalJsonFile.exists()) return null
            val jsonContent = internalJsonFile.readText()
            val jsonArray = JSONArray(jsonContent)
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                if (obj.getString("name") == name) {
                    val description = obj.optString("description", "")
                    val imagePath = File(context.filesDir, "assets/" + obj.getString("imagePath")).absolutePath
                    return description to imagePath
                }
            }
        } catch (e: Exception) {
            Log.e("CreateSpaceViewModel", "Error buscando modelo en models.json: ${e.message}")
        }
        return null
    }

    private fun loadModelsFromPrefsOrJson(context: Context) {
        val sharedPrefHelper = SharedPreferenceHelper(context)
        val jsonString = sharedPrefHelper.getStringData("furniture_list_json")
        if (!jsonString.isNullOrEmpty()) {
            try {
                val modelsArray = JSONArray(jsonString)
                val modelsList = mutableListOf<FurniturePreviewModel>()
                for (i in 0 until modelsArray.length()) {
                    val modelObject = modelsArray.getJSONObject(i)
                    val name = modelObject.getString("name")
                    val type = modelObject.getString("path")
                    val details = getModelDetailsByName(name, context)
                    modelsList.add(
                        FurniturePreviewModel(
                            name = name,
                            type = type,
                            description = details?.first,
                            imagePath = details?.second
                        )
                    )
                }
                _furnitureList.value = modelsList
                sharedPrefHelper.saveStringData("furniture_list_json", null) // Limpiar después de usar
                Log.d("CreateSpaceViewModel", "Lista de modelos cargada desde SharedPreferences: ${modelsList.size} elementos")
                return
            } catch (e: Exception) {
                Log.e("CreateSpaceViewModel", "Error al cargar la lista de modelos desde SharedPreferences: ${e.message}")
            }
        }
        loadModelsFromJson(context)
    }
}
