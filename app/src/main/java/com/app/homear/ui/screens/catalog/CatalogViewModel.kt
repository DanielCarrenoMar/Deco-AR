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
import com.app.homear.domain.usecase.firestore.GetAllCollectionFurnitureUseCase
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
    private val getAllCollectionFurnitureUseCase: GetAllCollectionFurnitureUseCase
): ViewModel() {
    var furnitureItems by mutableStateOf<List<FurnitureItem>>(emptyList())
        private set
    var searchQuery by mutableStateOf("")
    var isGridView by mutableStateOf(true)
    val filteredItems: List<FurnitureItem>
        get() = furnitureItems.filter {
            it.name.contains(searchQuery, ignoreCase = true) ||
                    it.description.contains(searchQuery, ignoreCase = true)
        }


    fun test() {
        var collet: MutableList<FurnitureEntity> = mutableListOf()
        viewModelScope.launch {
            getAllCollectionFurnitureUseCase.invoke().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        collet = result.data as MutableList<FurnitureEntity>
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Error<*> -> TODO()
                }

            }
            Log.w("test", "test: $collet")
        }

    }


        init {
            // Inicializar con los modelos disponibles
            furnitureItems = listOf(
                FurnitureItem(
                    id = 1,
                    name = "Mueble Moderno",
                    description = "Mueble elegante y contemporáneo",
                    modelPath = "models/Mueble-1.glb",
                    colors = listOf("#8B4513", "#A0522D", "#D2691E")
                ),
                FurnitureItem(
                    id = 2,
                    name = "BoomBox Retro",
                    description = "Equipo de sonido vintage",
                    modelPath = "models/BoomBox.glb",
                    colors = listOf("#000000", "#FF0000", "#4169E1")
                ),
                FurnitureItem(
                    id = 3,
                    name = "Caja Decorativa",
                    description = "Caja minimalista para almacenamiento",
                    modelPath = "models/Box.glb",
                    colors = listOf("#8B4513", "#DEB887", "#D2691E")
                ),
                FurnitureItem(
                    id = 4,
                    name = "Decoración Apple",
                    description = "Elemento decorativo moderno",
                    modelPath = "models/apple.glb",
                    colors = listOf("#FF0000", "#008000", "#FFD700")
                ),
                FurnitureItem(
                    id = 5,
                    name = "Pato Decorativo",
                    description = "Figura decorativa única",
                    modelPath = "models/Duck.glb",
                    colors = listOf("#FFD700", "#FFA500", "#FF4500")
                )
            )
        }

}