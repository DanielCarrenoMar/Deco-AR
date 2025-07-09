package com.app.homear.ui.screens.addProducto

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.model.Superficie
import com.app.homear.domain.usecase.firestore.AddFurnitureUseCase
import com.app.homear.domain.usecase.firestore.GetCurrentUserUseCase
import com.app.homear.domain.usecase.remoteStorage.UploadImageToRemoteUseCase
import com.app.homear.domain.usecase.remoteStorage.UploadModelToRemoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.io.File
import java.io.InputStream
import javax.inject.Inject

data class AddProductState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null,
    val currentUserId: String? = null
)

@HiltViewModel
class AddProductoViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val uploadImageToRemoteUseCase: UploadImageToRemoteUseCase,
    private val uploadModelToRemoteUseCase: UploadModelToRemoteUseCase,
    private val addFurnitureUseCase: AddFurnitureUseCase,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var state by mutableStateOf(AddProductState())
        private set

    init {
        getCurrentUser()
    }

    private fun getCurrentUser() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        state = state.copy(currentUserId = result.data?.key)
                        Log.d("AddProductoViewModel", "Current user ID: ${result.data?.key}")
                    }

                    is Resource.Error -> {
                        Log.e(
                            "AddProductoViewModel",
                            "Error getting current user: ${result.message}"
                        )
                        state = state.copy(errorMessage = "Error al obtener usuario actual")
                    }

                    is Resource.Loading -> {
                        // Loading state is handled globally
                    }
                }
            }
        }
    }

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
        if (state.currentUserId == null) {
            state = state.copy(errorMessage = "Usuario no identificado")
            return
        }

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

                // 1. Subir imagen
                val imageBytes = uriToByteArray(Uri.parse(imageUri))
                val imageName = "furniture_image_${System.currentTimeMillis()}.jpg"
                var imageUrl: String? = null

                uploadImageToRemoteUseCase(imageName, imageBytes).collect { imageResult ->
                    when (imageResult) {
                        is Resource.Success -> {
                            imageUrl = imageResult.data
                            Log.d("AddProductoViewModel", "Image uploaded successfully: $imageUrl")
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                errorMessage = "Error al subir imagen: ${imageResult.message}"
                            )
                            return@collect
                        }

                        is Resource.Loading -> {
                            Log.d("AddProductoViewModel", "Uploading image...")
                        }
                    }
                }

                if (imageUrl == null) return@launch

                // 2. Subir modelo 3D
                val modelBytes = uriToByteArray(Uri.parse(modelUri))
                val modelName = "furniture_model_${System.currentTimeMillis()}.glb"
                var modelUrl: String? = null

                uploadModelToRemoteUseCase(modelName, modelBytes).collect { modelResult ->
                    when (modelResult) {
                        is Resource.Success -> {
                            modelUrl = modelResult.data
                            Log.d("AddProductoViewModel", "Model uploaded successfully: $modelUrl")
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                errorMessage = "Error al subir modelo: ${modelResult.message}"
                            )
                            return@collect
                        }

                        is Resource.Loading -> {
                            Log.d("AddProductoViewModel", "Uploading model...")
                        }
                    }
                }

                if (modelUrl == null) return@launch

                // 3. Crear FurnitureModel
                val materialsSet = materials.split(",").map { it.trim() }.toHashSet()
                val keywordsSet = hashSetOf(name.lowercase(), description.lowercase())
                materialsSet.forEach { keywordsSet.add(it.lowercase()) }

                val furnitureModel = FurnitureModel(
                    name = name,
                    description = description,
                    material = materialsSet,
                    keywords = keywordsSet,
                    modelFile = File(modelUrl!!),
                    imageFile = File(imageUrl!!),
                    height = heightFloat,
                    width = widthFloat,
                    length = lengthFloat,
                    superficie = superficie
                )

                // 4. Guardar en Firestore
                addFurnitureUseCase(furnitureModel).collect { result ->
                    when (result) {
                        is Resource.Success -> {
                            state = state.copy(
                                isLoading = false,
                                isSuccess = true
                            )
                            Log.d(
                                "AddProductoViewModel",
                                "Furniture added successfully to Firestore"
                            )
                        }

                        is Resource.Error -> {
                            state = state.copy(
                                isLoading = false,
                                errorMessage = "Error al guardar en base de datos: ${result.message}"
                            )
                        }

                        is Resource.Loading -> {
                            Log.d("AddProductoViewModel", "Saving to Firestore...")
                        }
                    }
                }

            } catch (e: Exception) {
                state = state.copy(
                    isLoading = false,
                    errorMessage = "Error inesperado: ${e.message}"
                )
                Log.e("AddProductoViewModel", "Unexpected error in addProduct", e)
            }
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