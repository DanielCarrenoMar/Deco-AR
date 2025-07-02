package com.app.homear.ui.screens.catalog

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.DriveFileModel
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.usecase.firestore.GetAllCollectionFurnitureUseCase
import com.app.homear.domain.usecase.remoteStorage.DeleteFileFromRemoteByIdUseCase
import com.app.homear.domain.usecase.remoteStorage.GetAllFurnituresFromRemoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FurnitureItem(
    val id: Int,
    val name: String,
    val description: String,
    val modelPath: String,
    val colors: List<String>
)

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAllCollectionFurnitureUseCase: GetAllCollectionFurnitureUseCase,
    private val getAllFurnituresFromRemoteStorageUseCase: GetAllFurnituresFromRemoteUseCase,
    private val deleteFileFromRemoteByIdUseCase: DeleteFileFromRemoteByIdUseCase
): ViewModel() {
    var furnitureItems by mutableStateOf<List<FurnitureItem>>(emptyList())
        private set
    var searchQuery by mutableStateOf("")
    var isGridView by mutableStateOf(true)
    var isLoading by mutableStateOf(true)
        private set
    val filteredItems: List<FurnitureItem>
        get() = furnitureItems.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }

    fun loadFurnitureData() {
        viewModelScope.launch {
            getAllCollectionFurnitureUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {

                        val furnitureList: List<FurnitureModel> = result.data!!
                        furnitureItems = furnitureList.mapIndexed { index, model ->
                            FurnitureItem(
                                id = index + 1, // Generar ID basado en el índice
                                name = model.name,
                                description = model.description,
                                modelPath = model.modelFile.path,
                                colors = listOf("#8B4513", "#A0522D", "#D2691E")
                            )
                        }
                        isLoading = false
                    }

                    is Resource.Loading -> {
                        isLoading = true
                    }

                    is Resource.Error<*> -> {
                        Log.e(
                            "CatalogViewModel",
                            "Error loading furniture data: ${result.message}"
                        )
                        // Agregar datos de prueba como fallback
                        furnitureItems = listOf(
                            FurnitureItem(
                                id = 1,
                                name = "Mueble de Prueba",
                                description = "Datos de prueba - Firestore no disponible",
                                modelPath = "models/test.glb",
                                colors = listOf("#FF0000", "#00FF00", "#0000FF")
                            ),
                            FurnitureItem(
                                id = 2,
                                name = "Silla de Prueba",
                                description = "Datos de prueba - Error en carga",
                                modelPath = "models/test2.glb",
                                colors = listOf("#FFD700", "#FFA500", "#FF4500")
                            )
                        )
                        isLoading = false
                    }
                }
            }
        }
    }

    public fun testRemoteFurnitureData() {
        viewModelScope.launch {
            Log.i("DRIVE", "Haciendo peticion")
            deleteFileFromRemoteByIdUseCase("1AeuMWLClpHvuX8K60JSthFHaJ_1M-Q55").collect{result ->
                when (result) {
                    is Resource.Success -> {
                        Log.i("DRIVE", "Remote furniture response: ${result.data!!}")
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Error<*> -> {
                        Log.e(
                            "CatalogViewModel",
                            "Error loading remote furniture data: ${result.message}"
                        )
                    }
                }
            }
        }
    }

    fun clearSearch() {
        searchQuery = ""
    }

    fun toggleViewMode() {
        isGridView = !isGridView
    }

    fun onItemSelected(item: FurnitureItem) {
        Log.d("CatalogViewModel", "Item selected: ${item.name}")
        // TODO: Implement navigation to detail screen
    }

    fun onItemAddToCart(item: FurnitureItem) {
        Log.d("CatalogViewModel", "Item added to favorites/cart: ${item.name}")
        // TODO: Implement add to favorites/cart functionality
    }

    init {
        // Inicializar con lista vacía - los datos se cargarán desde Firestore
        furnitureItems = emptyList()
        Log.d("CatalogViewModel", "ViewModel initialized, will load from Firestore")
    }
}