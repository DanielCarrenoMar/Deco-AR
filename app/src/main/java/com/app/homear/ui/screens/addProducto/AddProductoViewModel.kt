package com.app.homear.ui.screens.addProducto

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.Superficie
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import javax.inject.Inject
import org.json.JSONArray
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileOutputStream

data class AddProductState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)

data class LocalFurnitureData(
    val name: String,
    val description: String,
    val height: Float,
    val width: Float,
    val length: Float,
    val materials: String,
    val superficie: String,
    val modelPath: String,
    val imagePath: String
)

@HiltViewModel
class AddProductoViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    var state by mutableStateOf(AddProductState())
        private set

    fun addProduct(
        modelUri: String,
        imageUri: String,
        name: String,
        description: String,
        height: String,
        width: String,
        length: String,
        materials: String,
        superficie: Superficie = Superficie.PISO
    ) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, errorMessage = null)

            try {
                // Validar campos requeridos
                if (modelUri.isEmpty() || imageUri.isEmpty() || name.isEmpty() ||
                    description.isEmpty() || height.isEmpty() || width.isEmpty() ||
                    length.isEmpty() || materials.isEmpty()
                ) {
                    state = state.copy(
                        isLoading = false,
                        errorMessage = "Todos los campos son obligatorios"
                    )
                    return@launch
                }

                // Convertir dimensiones
                val heightFloat = height.replace(",", ".").toFloatOrNull()
                val widthFloat = width.replace(",", ".").toFloatOrNull()
                val lengthFloat = length.replace(",", ".").toFloatOrNull()

                if (heightFloat == null || widthFloat == null || lengthFloat == null) {
                    state = state.copy(
                        isLoading = false,
                        errorMessage = "Las dimensiones deben ser números válidos"
                    )
                    return@launch
                }

                // 1. Copiar imagen a assets/imagen
                val imageBytes = uriToByteArray(Uri.parse(imageUri))
                val imageName = "furniture_image_${System.currentTimeMillis()}.jpg"
                val imagePath = "imagen/$imageName"
                
                val imageFile = File(context.filesDir, "assets/imagen/$imageName")
                imageFile.parentFile?.mkdirs()
                FileOutputStream(imageFile).use { it.write(imageBytes) }
                
                Log.d("AddProductoViewModel", "Image saved locally: ${imageFile.absolutePath}")

                // 2. Copiar modelo 3D a assets/models
                val modelBytes = uriToByteArray(Uri.parse(modelUri))
                val modelName = "furniture_model_${System.currentTimeMillis()}.glb"
                val modelPath = "models/$modelName"
                
                val modelFile = File(context.filesDir, "assets/models/$modelName")
                modelFile.parentFile?.mkdirs()
                FileOutputStream(modelFile).use { it.write(modelBytes) }
                
                Log.d("AddProductoViewModel", "Model saved locally: ${modelFile.absolutePath}")

                // 3. Crear objeto de datos del mueble
                val furnitureData = LocalFurnitureData(
                    name = name,
                    description = description,
                    height = heightFloat,
                    width = widthFloat,
                    length = lengthFloat,
                    materials = materials,
                    superficie = superficie.name,
                    modelPath = modelPath,
                    imagePath = imagePath
                )

                // 4. Actualizar JSON con el nuevo mueble
                updateLocalFurnitureJSON(furnitureData)

                state = state.copy(
                    isLoading = false,
                    isSuccess = true
                )
                Log.d("AddProductoViewModel", "Furniture added successfully to local storage")

            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    errorMessage = "Error inesperado: ${e.message}"
                )
                Log.e("AddProductoViewModel", "Unexpected error in addProduct", e)
            }
        }
    }

    private fun updateLocalFurnitureJSON(newFurniture: LocalFurnitureData) {
        try {
            val jsonFile = File(context.filesDir, "assets/models.json")
            jsonFile.parentFile?.mkdirs()
            
            val jsonArray = if (jsonFile.exists()) {
                val content = jsonFile.readText()
                if (content.isNotEmpty()) JSONArray(content) else JSONArray()
            } else {
                JSONArray()
            }

            // Crear objeto JSON para el nuevo mueble
            val furnitureJson = JSONObject().apply {
                put("name", newFurniture.name)
                put("description", newFurniture.description)
                put("height", newFurniture.height)
                put("width", newFurniture.width)
                put("length", newFurniture.length)
                put("materials", newFurniture.materials)
                put("superficie", newFurniture.superficie)
                put("modelPath", newFurniture.modelPath)
                put("imagePath", newFurniture.imagePath)
            }

            jsonArray.put(furnitureJson)
            
            // Guardar JSON actualizado
            jsonFile.writeText(jsonArray.toString())
            Log.d("AddProductoViewModel", "JSON updated successfully: ${jsonFile.absolutePath}")
            
        } catch (e: Exception) {
            Log.e("AddProductoViewModel", "Error updating JSON", e)
            throw e
        }
    }

    private fun uriToByteArray(uri: Uri): ByteArray {
        val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
        return inputStream?.readBytes() ?: throw Exception("No se pudo leer el archivo")
    }

    fun clearState() {
        state = state.copy(
            isSuccess = false,
            errorMessage = null
        )
    }

    fun clearError() {
        state = state.copy(errorMessage = null)
    }
}