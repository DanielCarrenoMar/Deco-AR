package com.app.homear.ui.screens.catalog

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.DriveFileModel
import com.app.homear.domain.model.FurnitureModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.model.Superficie
import com.app.homear.domain.usecase.firestore.GetAllCollectionFurnitureUseCase
import com.app.homear.domain.usecase.remoteStorage.DeleteFileFromRemoteByIdUseCase
import com.app.homear.domain.usecase.remoteStorage.DownloadFileFromRemoteByIdUseCase
import com.app.homear.domain.usecase.remoteStorage.DownloadImageFromRemoteByNameUseCase
import com.app.homear.domain.usecase.remoteStorage.GetAllFurnituresFromRemoteUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import java.io.File
import javax.inject.Inject

data class FurnitureItem(
    val id: Int,
    val name: String,
    val description: String,
    val modelPath: String,
    val imagePath: String,
    val colors: List<String>,
    val materials: Set<String>,
    val height: Float,
    val width: Float,
    val length: Float,
    val superficie: Superficie
)

data class FilterState(
    val selectedMaterials: Set<String> = emptySet(),
    val minHeight: Float? = null,
    val maxHeight: Float? = null,
    val minWidth: Float? = null,
    val maxWidth: Float? = null,
    val minLength: Float? = null,
    val maxLength: Float? = null,
    val selectedSuperficie: Superficie? = null
)

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAllCollectionFurnitureUseCase: GetAllCollectionFurnitureUseCase,
    private val downloadImageFromRemoteByNameUseCase: DownloadImageFromRemoteByNameUseCase,
    @ApplicationContext private val context: Context
): ViewModel() {
    var furnitureItems by mutableStateOf<List<FurnitureItem>>(emptyList())
        private set
    var searchQuery by mutableStateOf("")
    var isGridView by mutableStateOf(true)
    var isLoading by mutableStateOf(true)
        private set
    var showFilters by mutableStateOf(false)
    var filterState by mutableStateOf(FilterState())
        private set

    // Available filter options
    val availableMaterials: Set<String>
        get() = furnitureItems.flatMap { it.materials }.toSet()

    val availableSuperficies: List<Superficie>
        get() = Superficie.values().toList()

    val filteredItems: List<FurnitureItem>
        get() = furnitureItems.filter { item ->
            // Search filter
            val matchesSearch = if (searchQuery.isBlank()) true else {
                item.name.contains(searchQuery, ignoreCase = true) ||
                        item.description.contains(searchQuery, ignoreCase = true)
            }

            // Material filter
            val matchesMaterial = if (filterState.selectedMaterials.isEmpty()) true else {
                item.materials.any { material -> filterState.selectedMaterials.contains(material) }
            }

            // Dimension filters
            val matchesHeight = (filterState.minHeight?.let { item.height >= it } ?: true) &&
                    (filterState.maxHeight?.let { item.height <= it } ?: true)

            val matchesWidth = (filterState.minWidth?.let { item.width >= it } ?: true) &&
                    (filterState.maxWidth?.let { item.width <= it } ?: true)

            val matchesLength = (filterState.minLength?.let { item.length >= it } ?: true) &&
                    (filterState.maxLength?.let { item.length <= it } ?: true)

            // Surface filter
            val matchesSuperficie = filterState.selectedSuperficie?.let { selected ->
                item.superficie == selected || item.superficie == Superficie.TODAS
            } ?: true

            matchesSearch && matchesMaterial && matchesHeight && matchesWidth && matchesLength && matchesSuperficie
        }

    fun loadFurnitureData() {
        viewModelScope.launch {
            getAllCollectionFurnitureUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val furnitureList: List<FurnitureModel> = result.data!!

                        // Crear los items iniciales con rutas temporales
                        val initialItems = furnitureList.mapIndexed { index, model ->
                            FurnitureItem(
                                id = index + 1,
                                name = model.name,
                                description = model.description,
                                modelPath = model.modelFile.path,
                                imagePath = model.imageFile.path, // Ruta temporal
                                colors = listOf("#8B4513", "#A0522D", "#D2691E"),
                                materials = model.material,
                                height = model.height,
                                width = model.width,
                                length = model.length,
                                superficie = model.superficie
                            )
                        }

                        furnitureItems = initialItems
                        isLoading = false

                        // Descargar imágenes en paralelo
                        downloadImagesForFurniture(furnitureList)
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
                                imagePath = "images/test.jpg",
                                colors = listOf("#FF0000", "#00FF00", "#0000FF"),
                                materials = setOf("Madera", "Metal"),
                                height = 1.2f,
                                width = 0.8f,
                                length = 0.6f,
                                superficie = Superficie.PISO
                            ),
                            FurnitureItem(
                                id = 2,
                                name = "Silla de Prueba",
                                description = "Datos de prueba - Error en carga",
                                modelPath = "models/test2.glb",
                                imagePath = "images/test2.jpg",
                                colors = listOf("#FFD700", "#FFA500", "#FF4500"),
                                materials = setOf("Plástico", "Tela"),
                                height = 0.9f,
                                width = 0.5f,
                                length = 0.5f,
                                superficie = Superficie.PISO
                            )
                        )
                        isLoading = false
                    }
                }
            }
        }
    }

    private fun downloadImagesForFurniture(furnitureList: List<FurnitureModel>) {
        viewModelScope.launch {
            try {
                // Descargar todas las imágenes en paralelo
                val downloadJobs = furnitureList.mapIndexed { index, model ->
                    async {
                        try {
                            val imageName = model.imageFile.name
                            Log.d("CatalogViewModel", "Downloading image: $imageName")

                            downloadImageFromRemoteByNameUseCase(
                                imageName,
                                context
                            ).collect { imageResult ->
                                when (imageResult) {
                                    is Resource.Success -> {
                                        val localImagePath = imageResult.data!!.absolutePath
                                        Log.d(
                                            "CatalogViewModel",
                                            "Image downloaded successfully: $localImagePath"
                                        )

                                        // Actualizar el item con la ruta local
                                        updateFurnitureItemImage(index, localImagePath)
                                    }

                                    is Resource.Error<*> -> {
                                        Log.e(
                                            "CatalogViewModel",
                                            "Error downloading image $imageName: ${imageResult.message}"
                                        )
                                    }

                                    is Resource.Loading -> {
                                        Log.d("CatalogViewModel", "Downloading image: $imageName")
                                    }
                                }
                            }
                        } catch (e: Exception) {
                            Log.e(
                                "CatalogViewModel",
                                "Error downloading image for furniture at index $index",
                                e
                            )
                        }
                    }
                }

                // Esperar a que todas las descargas terminen
                downloadJobs.awaitAll()

            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error in downloadImagesForFurniture", e)
            }
        }
    }

    private fun updateFurnitureItemImage(index: Int, localImagePath: String) {
        val currentItems = furnitureItems.toMutableList()
        if (index < currentItems.size) {
            currentItems[index] = currentItems[index].copy(imagePath = localImagePath)
            furnitureItems = currentItems
            Log.d(
                "CatalogViewModel",
                "Updated furniture item $index with local image path: $localImagePath"
            )
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

    fun toggleFilters() {
        showFilters = !showFilters
    }

    fun updateFilterState(newFilterState: FilterState) {
        filterState = newFilterState
    }

    fun clearFilters() {
        filterState = FilterState()
    }

    fun hasActiveFilters(): Boolean {
        return filterState.selectedMaterials.isNotEmpty() ||
                filterState.minHeight != null ||
                filterState.maxHeight != null ||
                filterState.minWidth != null ||
                filterState.maxWidth != null ||
                filterState.minLength != null ||
                filterState.maxLength != null ||
                filterState.selectedSuperficie != null
    }

    init {
        // Inicializar con lista vacía - los datos se cargarán desde Firestore
        furnitureItems = emptyList()
        Log.d("CatalogViewModel", "ViewModel initialized, will load from Firestore")
    }
}