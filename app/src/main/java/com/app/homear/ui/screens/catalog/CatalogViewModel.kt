package com.app.homear.ui.screens.catalog

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.homear.data.database.entity.FurnitureEntity
import com.app.homear.domain.model.Resource
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.usecase.firestore.GetAllCollectionFurnitureUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import kotlinx.coroutines.TimeoutCancellationException
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
    private val getAllCollectionFurnitureUseCase: GetAllCollectionFurnitureUseCase
): ViewModel() {
    var furnitureItems by mutableStateOf<List<FurnitureItem>>(emptyList())
        private set
    var searchQuery by mutableStateOf("")
    var isGridView by mutableStateOf(true)
    var isLoading by mutableStateOf(false)
        private set
    val filteredItems: List<FurnitureItem>
        get() = furnitureItems.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }

    fun loadFurnitureData() {
        Log.d("CatalogViewModel", "Starting to load furniture data...")
        viewModelScope.launch {
            try {
                isLoading = true
                Log.d("CatalogViewModel", "Set loading to true")

                withTimeout(10000) {
                    getAllCollectionFurnitureUseCase.invoke().collect { result ->
                        Log.d("CatalogViewModel", "Received result: ${result::class.simpleName}")
                        when (result) {
                            is Resource.Success -> {
                                val firestoreItems =
                                    result.data as? List<FurnitureModel> ?: emptyList()
                                Log.d(
                                    "CatalogViewModel",
                                    "Success! Raw data type: ${result.data?.javaClass?.simpleName}"
                                )
                                Log.d(
                                    "CatalogViewModel",
                                    "Loaded ${firestoreItems.size} items from Firestore"
                                )

                                if (firestoreItems.isNotEmpty()) {
                                    Log.d(
                                        "CatalogViewModel",
                                        "First item: ${firestoreItems[0].name}"
                                    )
                                }

                                // Convertir FurnitureModel a FurnitureItem
                                furnitureItems = firestoreItems.mapIndexed { index, model ->
                                    Log.d("CatalogViewModel", "Converting: ${model.name}")
                                    FurnitureItem(
                                        id = index + 1, // Generar ID basado en el índice
                                        name = model.name,
                                        description = model.description,
                                        modelPath = model.modelFile.path,
                                        colors = listOf("#8B4513", "#A0522D", "#D2691E")
                                    )
                                }

                                Log.d(
                                    "CatalogViewModel",
                                    "Final furnitureItems count: ${furnitureItems.size}"
                                )
                                isLoading = false
                            }

                            is Resource.Loading -> {
                                Log.d("CatalogViewModel", "Loading furniture data...")
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
                                Log.d(
                                    "CatalogViewModel",
                                    "Added fallback test data: ${furnitureItems.size} items"
                                )
                                isLoading = false
                            }
                        }
                    }
                }
            } catch (e: TimeoutCancellationException) {
                Log.e("CatalogViewModel", "Timeout loading furniture data")
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
                Log.d(
                    "CatalogViewModel",
                    "Added fallback test data: ${furnitureItems.size} items"
                )
                isLoading = false
            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Exception loading furniture data: ${e.message}")
                Log.e("CatalogViewModel", "Exception stack trace: ", e)
                isLoading = false
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