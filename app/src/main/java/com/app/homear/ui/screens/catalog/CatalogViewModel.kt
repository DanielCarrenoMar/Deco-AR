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
import com.app.homear.domain.usecase.firestore.GetCurrentUserUseCase
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

data class ARModel(
    val name: String,
    val modelPath: String
)

@HiltViewModel
class CatalogViewModel @Inject constructor(
    private val getAllCollectionFurnitureUseCase: GetAllCollectionFurnitureUseCase,
    private val downloadImageFromRemoteByNameUseCase: DownloadImageFromRemoteByNameUseCase,
    private val downloadFurnitureFromRemoteByNameUseCase: DownloadFurnitureFromRemoteByNameUseCase,
    private val getTodosLosModelosUseCase: GetAllFurnituresFromRemoteUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
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
    var isProvider by mutableStateOf(false)
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

    var showItemModal by mutableStateOf(false)
        private set
    var selectedItem by mutableStateOf<FurnitureItem?>(null)
        private set

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
                        downloadModelsForFurniture(furnitureList)
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
                            updateFurnitureItemImage(index, localImagePath)
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
                                                updateFurnitureItemImage(index, localImagePath)
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
        val maxConcurrentDownloads = 5 // Limitar descargas simultáneas
        val semaphore = Semaphore(maxConcurrentDownloads)

        viewModelScope.launch {
            try {
                val downloadJobs = furnitureList.mapIndexed { index, model ->
                    async {
                        val modelName = model.modelFile.name

                        // Verificar si el modelo ya existe localmente
                        val localModelFile = File(
                            context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
                            "models/$modelName"
                        )

                        if (localModelFile.exists() && localModelFile.isFile) {
                            // El modelo ya existe localmente, usar esa ruta
                            val localModelPath = "models/$modelName"
                            Log.d("CatalogViewModel", "Model found locally: $localModelPath")
                            updateFurnitureItemModel(index, localModelPath)
                        } else {
                            // Descargar el modelo usando concurrencia limitada
                            semaphore.withPermit {
                                try {
                                    downloadFurnitureFromRemoteByNameUseCase(
                                        modelName,
                                        context
                                    ).collect { modelResult ->
                                        when (modelResult) {
                                            is Resource.Success -> {
                                                val localModelPath = "models/$modelName"
                                                Log.d("CatalogViewModel", "Model downloaded successfully: $localModelPath")
                                                updateFurnitureItemModel(index, localModelPath)
                                            }
                                            is Resource.Error<*> -> {
                                                Log.e("CatalogViewModel", "Error downloading model: ${modelResult.message}")
                                            }
                                            else -> {}
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e("CatalogViewModel", "Exception downloading model: ${e.message}")
                                }
                            }
                        }
                    }
                }
                // Esperar a que todas las descargas terminen
                downloadJobs.awaitAll()
            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error general en descarga de modelos: ${e.message}")
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

    private fun updateFurnitureItemModel(index: Int, modelPath: String) {
        if (index < furnitureItems.size) {
            furnitureItems = furnitureItems.toMutableList().apply {
                this[index] = this[index].copy(modelPath = modelPath)
            }
            Log.d("CatalogViewModel", "Updated model path for item $index: $modelPath")
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
        selectedItem = item
        showItemModal = true
    }

    fun onItemAddToCart(item: FurnitureItem) {
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

    fun checkIfProvider() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val user = result.data
                        isProvider = user?.type == "PROVIDER"
                    }
                    else -> Unit // podrías capturar el error si quieres
                }
            }
        }
    }

    companion object {
        // Lista de modelos disponibles en assets
        private val availableModels = listOf(
            FurnitureItem(
                id = 1,
                name = "BoomBox Retro",
                description = "Boombox retro con diseño vintage",
                modelPath = "models/BoomBox.glb",
                imagePath = "models/BoomBox_0.png",
                colors = listOf("#000000"),
                materials = setOf("Plástico", "Metal"),
                height = 0.3f,
                width = 0.4f,
                length = 0.2f,
                superficie = Superficie.PISO
            ),
            FurnitureItem(
                id = 2,
                name = "Módulo de Escritorio",
                description = "Módulo de escritorio moderno",
                modelPath = "models/modulo_escritorio.glb",
                imagePath = "models/modulo_escritorio.png",
                colors = listOf("#8B4513"),
                materials = setOf("Madera"),
                height = 0.75f,
                width = 1.2f,
                length = 0.6f,
                superficie = Superficie.PISO
            ),
            FurnitureItem(
                id = 3,
                name = "Silla Gris",
                description = "Silla moderna en tono gris",
                modelPath = "models/silla_gris.glb",
                imagePath = "models/silla_gris.png",
                colors = listOf("#808080"),
                materials = setOf("Metal", "Tela"),
                height = 0.9f,
                width = 0.45f,
                length = 0.45f,
                superficie = Superficie.PISO
            ),
            FurnitureItem(
                id = 4,
                name = "Silla de Oficina Verde",
                description = "Silla ergonómica de oficina",
                modelPath = "models/silla_de_oficina_verde.glb",
                imagePath = "models/silla_de_oficina_verde.png",
                colors = listOf("#008000"),
                materials = setOf("Metal", "Tela"),
                height = 1.1f,
                width = 0.6f,
                length = 0.6f,
                superficie = Superficie.PISO
            ),
            FurnitureItem(
                id = 5,
                name = "Mesa de TV",
                description = "Mesa de TV en madera",
                modelPath = "models/mesa_de_tv_madera.glb",
                imagePath = "models/mesa_de_tv_madera.png",
                colors = listOf("#8B4513"),
                materials = setOf("Madera"),
                height = 0.5f,
                width = 1.5f,
                length = 0.4f,
                superficie = Superficie.PISO
            ),
            FurnitureItem(
                id = 6,
                name = "Nevera Dos Puertas",
                description = "Refrigerador de dos puertas",
                modelPath = "models/nevera_dos_puertas.glb",
                imagePath = "models/nevera_dos_puertas.png",
                colors = listOf("#FFFFFF"),
                materials = setOf("Metal"),
                height = 1.8f,
                width = 0.9f,
                length = 0.7f,
                superficie = Superficie.PISO
            ),
            FurnitureItem(
                id = 7,
                name = "Mesa de Noche",
                description = "Mesa de noche clásica",
                modelPath = "models/mesa_de_noche.glb",
                imagePath = "models/mesa_de_noche.png",
                colors = listOf("#8B4513"),
                materials = setOf("Madera"),
                height = 0.6f,
                width = 0.4f,
                length = 0.4f,
                superficie = Superficie.PISO
            ),
            FurnitureItem(
                id = 8,
                name = "Cama Azul",
                description = "Cama individual con tapizado azul",
                modelPath = "models/cama_azul.glb",
                imagePath = "models/cama_azul.png",
                colors = listOf("#0000FF"),
                materials = setOf("Madera", "Tela"),
                height = 0.5f,
                width = 2.0f,
                length = 1.0f,
                superficie = Superficie.PISO
            ),
            FurnitureItem(
                id = 9,
                name = "TV Plano",
                description = "Televisor LED de pantalla plana",
                modelPath = "models/tv_plano.glb",
                imagePath = "models/tv_plano.png",
                colors = listOf("#000000"),
                materials = setOf("Metal", "Plástico"),
                height = 0.6f,
                width = 1.2f,
                length = 0.1f,
                superficie = Superficie.PARED
            ),
            FurnitureItem(
                id = 10,
                name = "Closet de Madera",
                description = "Armario de madera clásico",
                modelPath = "models/closet_de_madera.glb",
                imagePath = "models/closet_de_madera.png",
                colors = listOf("#8B4513"),
                materials = setOf("Madera"),
                height = 2.0f,
                width = 1.5f,
                length = 0.6f,
                superficie = Superficie.PISO
            )
        )
    }

    init {
        // Cargar los modelos predefinidos al inicializar
        loadPredefinedModels()
    }

    private fun loadPredefinedModels() {
        viewModelScope.launch {
            // Agregar los modelos predefinidos a la lista de muebles
            furnitureItems = availableModels
            
            // Cargar cada modelo en el CameraViewModel
            availableModels.forEach { item ->
                CameraViewModel.addARModelFromFurniture(item)
            }
            
            isLoading = false
        }
    }
}
