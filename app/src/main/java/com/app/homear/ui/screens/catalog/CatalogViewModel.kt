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
import com.app.homear.domain.usecase.remoteStorage.DownloadFurnitureFromRemoteByNameUseCase
import com.app.homear.domain.usecase.remoteStorage.DownloadImageFromRemoteByNameUseCase
import com.app.homear.domain.usecase.remoteStorage.GetAllFurnituresFromRemoteUseCase
import com.app.homear.ui.screens.camera.CameraViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import java.io.File
import javax.inject.Inject

data class FurnitureModel(
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

data class ARModel(
    val name: String,
    val modelPath: String
)

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAllCollectionFurnitureUseCase: GetAllCollectionFurnitureUseCase,
    private val downloadImageFromRemoteByNameUseCase: DownloadImageFromRemoteByNameUseCase,
    private val downloadFurnitureFromRemoteByNameUseCase: DownloadFurnitureFromRemoteByNameUseCase,
    @ApplicationContext private val context: Context
): ViewModel() {
    var furnitureItems by mutableStateOf<List<FurnitureModel>>(emptyList())
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

    val filteredItems: List<FurnitureModel>
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

    var showItemModal by mutableStateOf(false)
        private set
    var selectedItem by mutableStateOf<FurnitureModel?>(null)
        private set

    fun loadFurnitureData() {
        viewModelScope.launch {
            getAllCollectionFurnitureUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        furnitureItems = result.data!!
                        isLoading = false

                        // Descargar imágenes en paralelo
                        downloadImagesForFurniture(furnitureItems)
                        downloadModelsForFurniture(furnitureItems)
                    }

                    is Resource.Loading -> {
                        isLoading = true
                    }

                    is Resource.Error<*> -> {
                        Log.e(
                            "CatalogViewModel",
                            "Error loading furniture data: ${result.message}"
                        )
                        furnitureItems = emptyList()
                        isLoading = false
                    }
                }
            }
        }
    }

    private fun downloadImagesForFurniture(furnitureList: List<FurnitureModel>) {
        // Limitar la concurrencia máxima de descargas simultáneas
        val maxConcurrentDownloads = 10 // Puedes ajustar este valor según necesidades
        val semaphore = Semaphore(maxConcurrentDownloads)

        viewModelScope.launch {
            try {
                val downloadJobs = furnitureList.mapIndexed { index, model ->
                    async {
                        val imageName = model.imageFile.name

                        // 1. Verificar si la imagen ya existe localmente (sin usar semáforo)
                        val localImageFile = File(
                            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                            imageName
                        )

                        if (localImageFile.exists() && localImageFile.isFile()) {
                            // La imagen ya existe localmente, usar esa ruta
                            val localImagePath = localImageFile.absolutePath
                            Log.d(
                                "CatalogViewModel",
                                "Image found locally: $localImagePath"
                            )
                            updateFurnitureModelImage(index, localImagePath)
                        } else {
                            // 2. Descargar la imagen usando concurrencia limitada
                            semaphore.withPermit {
                                try {

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
                                                updateFurnitureModelImage(index, localImagePath)
                                            }

                                            is Resource.Error<*> -> {
                                                Log.e(
                                                    "CatalogViewModel",
                                                    "Error downloading image $imageName: ${imageResult.message}"
                                                )
                                            }

                                            is Resource.Loading -> {
                                                Log.d(
                                                    "CatalogViewModel",
                                                    "Downloading image: $imageName"
                                                )
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e(
                                        "CatalogViewModel",
                                        "Error processing image for furniture at index $index",
                                        e
                                    )
                                }
                            }
                        }
                    }
                }

                // Esperar a que todas las operaciones terminen
                downloadJobs.awaitAll()

            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error in downloadImagesForFurniture", e)
            }
        }
    }

    private fun downloadModelsForFurniture(furnitureList: List<FurnitureModel>) {
        val maxConcurrentDownloads = 10 // Puedes ajustar este valor
        val semaphore = Semaphore(maxConcurrentDownloads)

        viewModelScope.launch {
            try {
                val downloadJobs = furnitureList.mapIndexed { index, model ->
                    async {
                        val modelName = model.modelFile.name
                        val localModelFile = File(
                            context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                            modelName
                        )
                        if (localModelFile.exists() && localModelFile.isFile) {
                            // Ya existe local
                            val localModelPath = localModelFile.absolutePath
                            updateFurnitureModelModel(index, localModelPath)
                        } else {
                            semaphore.withPermit {
                                try {
                                    downloadFurnitureFromRemoteByNameUseCase(
                                        modelName,
                                        context
                                    ).collect { result ->
                                        when (result) {
                                            is Resource.Success -> {
                                                val localModelPath = result.data!!.absolutePath
                                                updateFurnitureModelModel(index, localModelPath)
                                            }

                                            is Resource.Error<*> -> {
                                                Log.e(
                                                    "CatalogViewModel",
                                                    "Error downloading model $modelName: ${result.message}"
                                                )
                                            }

                                            is Resource.Loading -> {
                                                Log.d(
                                                    "CatalogViewModel",
                                                    "Downloading model: $modelName"
                                                )
                                            }
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e(
                                        "CatalogViewModel",
                                        "Error processing model for furniture at index $index",
                                        e
                                    )
                                }
                            }
                        }
                    }
                }
                downloadJobs.awaitAll()
            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error in downloadModelsForFurniture", e)
            }
        }
    }

    private fun updateFurnitureModelImage(index: Int, localImagePath: String) {
        val currentItems = furnitureItems.toMutableList()
        if (index < currentItems.size) {
            currentItems[index] = currentItems[index].copy(imageFile = File(localImagePath))
            furnitureItems = currentItems
            Log.d(
                "CatalogViewModel",
                "Updated furniture item $index with local image path: $localImagePath"
            )
        }
    }

    private fun updateFurnitureModelModel(index: Int, localModelPath: String) {
        val currentItems = furnitureItems.toMutableList()
        if (index < currentItems.size) {
            currentItems[index] = currentItems[index].copy(modelFile = File(localModelPath))
            furnitureItems = currentItems
            Log.d(
                "CatalogViewModel",
                "Updated furniture item $index with local model path: $localModelPath"
            )
        }
    }

    fun clearSearch() {
        searchQuery = ""
    }

    fun toggleViewMode() {
        isGridView = !isGridView
    }

    fun onItemSelected(item: FurnitureModel) {
        Log.d("CatalogViewModel", "Item selected: ${item.name}")
        selectedItem = item
        showItemModal = true
    }

    fun onItemAddToCart(item: FurnitureModel) {
        Log.d("CatalogViewModel", "Item added to favorites/cart: ${item.name}")
        CameraViewModel.addARModelFromFurniture(item)
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

    fun closeItemModal() {
        showItemModal = false
        selectedItem = null
    }

    fun confirmItemAction() {
        selectedItem?.let { item ->
            Log.d("CatalogViewModel", "Item confirmed for AR: ${item.name}")
            // TODO: Navigate to AR view with selected item
        }
        closeItemModal()
    }

    init {
        // Inicializar con lista vacía - los datos se cargarán desde Firestore
        furnitureItems = emptyList()
        Log.d("CatalogViewModel", "ViewModel initialized, will load from Firestore")
    }
}