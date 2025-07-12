package com.app.homear.ui.screens.catalog

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.Superficie
import com.app.homear.ui.screens.camera.CameraViewModel
import com.app.homear.domain.model.Resource
import com.app.homear.domain.usecase.firestore.GetCurrentUserUseCase
import com.app.homear.ui.screens.camera.ARModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import org.json.JSONArray
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

    val availableMaterials: Set<String>
        get() = furnitureItems.flatMap { it.materials }.toSet()

    val availableSuperficies: List<Superficie>
        get() = Superficie.values().toList()

    val filteredItems: List<FurnitureItem>
        get() = furnitureItems.filter { item ->
            val matchesSearch = if (searchQuery.isBlank()) true else {
                item.name.contains(searchQuery, ignoreCase = true) ||
                        item.description.contains(searchQuery, ignoreCase = true)
            }
            val matchesMaterial = if (filterState.selectedMaterials.isEmpty()) true else {
                item.materials.any { material -> filterState.selectedMaterials.contains(material) }
            }
            val matchesHeight = (filterState.minHeight?.let { item.height >= it } ?: true) &&
                    (filterState.maxHeight?.let { item.height <= it } ?: true)
            val matchesWidth = (filterState.minWidth?.let { item.width >= it } ?: true) &&
                    (filterState.maxWidth?.let { item.width <= it } ?: true)
            val matchesLength = (filterState.minLength?.let { item.length >= it } ?: true) &&
                    (filterState.maxLength?.let { item.length <= it } ?: true)
            val matchesSuperficie = filterState.selectedSuperficie?.let { selected ->
                item.superficie == selected || item.superficie == Superficie.TODAS
            } ?: true
            matchesSearch && matchesMaterial && matchesHeight && matchesWidth && matchesLength && matchesSuperficie
        }

    var showItemModal by mutableStateOf(false)
        private set
    var selectedItem by mutableStateOf<FurnitureItem?>(null)
        private set

    init {
        loadFurnitureData()
        checkIfProvider()
    }

    fun loadFurnitureData() {
        viewModelScope.launch {
            isLoading = true
            val items = mutableListOf<FurnitureItem>()
            try {
                // Primero intentar leer del almacenamiento interno
                val internalJsonFile = File(context.filesDir, "assets/models.json")
                val jsonContent = if (internalJsonFile.exists()) {
                    // Si existe en almacenamiento interno, leer de ahí
                    internalJsonFile.readText()
                } else {
                    try {
                        // Si no existe, leer del asset empaquetado
                        context.assets.open("models.json").bufferedReader().use { it.readText() }
                    } catch (e: Exception) {
                        // Si no existe el asset, usar un array vacío
                        "[]"
                    }
                }

                val jsonArray = JSONArray(jsonContent)
                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val id = i + 1
                    val name = obj.getString("name")
                    val description = obj.getString("description")
                    val modelPath = obj.getString("modelPath") // Mantener la ruta relativa
                    val imagePath = File(context.filesDir, "assets/" + obj.getString("imagePath")).absolutePath
                    val materials = obj.getString("materials").split(",").map { it.trim() }.toSet()
                    val height = obj.getDouble("height").toFloat()
                    val width = obj.getDouble("width").toFloat()
                    val length = obj.getDouble("length").toFloat()
                    val superficie = try { Superficie.valueOf(obj.getString("superficie")) } catch (_: Exception) { Superficie.PISO }
                    items.add(
                        FurnitureItem(
                            id = id,
                            name = name,
                            description = description,
                            modelPath = modelPath, // Usar la ruta relativa directamente
                            imagePath = imagePath,
                            colors = listOf("#8B4513", "#A0522D", "#D2691E"),
                            materials = materials,
                            height = height,
                            width = width,
                            length = length,
                            superficie = superficie
                        )
                    )
                }
                furnitureItems = items
            } catch (e: Exception) {
                Log.e("CatalogViewModel", "Error leyendo modelos", e)
                furnitureItems = emptyList()
            }
            isLoading = false
        }
    }

    fun checkIfProvider() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        val user = result.data
                        isProvider = user?.type == "PROVIDER"
                    }
                    else -> Unit
                }
            }
        }
    }

    fun clearSearch() { searchQuery = "" }
    fun toggleViewMode() { isGridView = !isGridView }
    fun onItemSelected(item: FurnitureItem) {
        Log.d("CatalogViewModel", "Item selected: ${item.name}")
        selectedItem = item
        showItemModal = true
    }
    fun onItemAddToCart(item: FurnitureItem) {
        Log.d("CatalogViewModel", "Item added to AR: ${item.name}")
        // Crear el modelo AR del mueble seleccionado
        val arModel = ARModel(
            name = item.name,
            modelPath = item.modelPath,
            imagePath = item.imagePath
        )
        
        // Verificar si el modelo ya existe en la lista
        val modelExists = CameraViewModel.sharedAvailableModels.any { 
            it.name == arModel.name && it.modelPath == arModel.modelPath 
        }
        
        // Solo agregar si no existe ya en la lista
        if (!modelExists) {
            CameraViewModel.sharedAvailableModels.add(arModel)
            Log.d("CatalogViewModel", "Mueble agregado al menú AR: ${arModel.name}")
        } else {
            Log.d("CatalogViewModel", "El mueble ya existe en el menú AR: ${arModel.name}")
        }
    }
    fun toggleFilters() { showFilters = !showFilters }
    fun updateFilterState(newFilterState: FilterState) { filterState = newFilterState }
    fun clearFilters() { filterState = FilterState() }
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
}