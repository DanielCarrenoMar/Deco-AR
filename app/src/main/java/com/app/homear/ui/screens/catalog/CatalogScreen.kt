package com.app.homear.ui.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.app.homear.ui.component.NavBar
import com.app.homear.ui.theme.CorporatePurple
import com.app.homear.domain.model.Superficie
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    navigateToTutorial: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToConfiguration: () -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadFurnitureData()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título del catálogo
            Text(
                text = "Catálogo",
                color = CorporatePurple,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            // Barra de búsqueda
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF2F2F2))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(24.dp)),
                contentAlignment = Alignment.CenterStart
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp)
                ) {
                    Icon(
                        Icons.Filled.Search,
                        contentDescription = "Buscar",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = viewModel.searchQuery,
                        onValueChange = { query ->
                            viewModel.searchQuery = query
                        },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (viewModel.searchQuery.isEmpty()) {
                                Text("Buscar muebles...", color = Color.Gray)
                            }
                            innerTextField()
                        }
                    )
                    if (viewModel.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearSearch() }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Limpiar búsqueda"
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Fila de acciones debajo de la barra de búsqueda
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono de filtro a la izquierda
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (viewModel.hasActiveFilters()) CorporatePurple.copy(alpha = 0.2f) else Color(
                                0xFFF2F2F2
                            )
                        )
                        .border(
                            1.dp,
                            if (viewModel.hasActiveFilters()) CorporatePurple else Color.LightGray,
                            RoundedCornerShape(12.dp)
                        )
                        .clickable {
                            viewModel.toggleFilters()
                        }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.FilterList,
                        contentDescription = "Filtrar",
                        tint = if (viewModel.hasActiveFilters()) CorporatePurple else Color.Black
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                // Selector de vista a la derecha
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Filled.GridView,
                        contentDescription = "Vista Grid",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewModel.isGridView) Color(0xFFE0E0E0) else Color.Transparent)
                            .clickable {
                                if (!viewModel.isGridView) {
                                    viewModel.isGridView = true
                                }
                            }
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(
                        Icons.Filled.List,
                        contentDescription = "Vista Lista",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!viewModel.isGridView) Color(0xFFE0E0E0) else Color.Transparent)
                            .clickable {
                                if (viewModel.isGridView) {
                                    viewModel.isGridView = false
                                }
                            }
                            .padding(4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lista o grid de items
            if (viewModel.isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                if (viewModel.isGridView) {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 80.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(viewModel.filteredItems) { item ->
                            FurnitureCard(
                                item = item,
                                onItemClick = {
                                    viewModel.onItemSelected(item)
                                },
                                onAddClick = {
                                    viewModel.onItemAddToCart(item)
                                }
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        items(viewModel.filteredItems) { item ->
                            FurnitureCard(
                                item = item,
                                onItemClick = {
                                    viewModel.onItemSelected(item)
                                },
                                onAddClick = {
                                    viewModel.onItemAddToCart(item)
                                },
                                isList = true
                            )
                        }
                    }
                }
                // Mensaje cuando no hay datos
                if (viewModel.filteredItems.isEmpty() && !viewModel.isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (viewModel.hasActiveFilters() || viewModel.searchQuery.isNotEmpty()) {
                                "No se encontraron muebles con los filtros aplicados"
                            } else {
                                "No hay muebles disponibles"
                            },
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Barra de navegación
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
            NavBar(
                toCamera = navigateToCamera,
                toTutorial = navigateToTutorial,
                toCatalog = null,
                toProfile = navigateToProfile,
                toConfiguration = navigateToConfiguration,
            )
        }

        // Filter Bottom Sheet
        if (viewModel.showFilters) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleFilters() },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ) {
                FilterBottomSheet(
                    viewModel = viewModel,
                    onDismiss = { viewModel.toggleFilters() }
                )
            }
        }
    }
}

@Composable
fun FilterBottomSheet(
    viewModel: CatalogViewModel,
    onDismiss: () -> Unit
) {
    var tempFilterState by remember { mutableStateOf(viewModel.filterState) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filtros",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onDismiss) {
                Icon(Icons.Filled.Close, contentDescription = "Cerrar")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Materials Filter
        if (viewModel.availableMaterials.isNotEmpty()) {
            Text(
                text = "Materiales",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyColumn(
                modifier = Modifier.height(100.dp)
            ) {
                items(viewModel.availableMaterials.toList()) { material ->
                    FilterChip(
                        onClick = {
                            tempFilterState = if (tempFilterState.selectedMaterials.contains(material)) {
                                tempFilterState.copy(
                                    selectedMaterials = tempFilterState.selectedMaterials - material
                                )
                            } else {
                                tempFilterState.copy(
                                    selectedMaterials = tempFilterState.selectedMaterials + material
                                )
                            }
                        },
                        label = { Text(material) },
                        selected = tempFilterState.selectedMaterials.contains(material),
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Surface Filter
        Text(
            text = "Superficie",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyColumn(
            modifier = Modifier.height(120.dp)
        ) {
            items(viewModel.availableSuperficies) { superficie ->
                FilterChip(
                    onClick = {
                        tempFilterState = if (tempFilterState.selectedSuperficie == superficie) {
                            tempFilterState.copy(selectedSuperficie = null)
                        } else {
                            tempFilterState.copy(selectedSuperficie = superficie)
                        }
                    },
                    label = { Text(superficie.name) },
                    selected = tempFilterState.selectedSuperficie == superficie,
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Dimensions Filter
        Text(
            text = "Dimensiones (metros)",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Height filter
        DimensionFilter(
            label = "Altura",
            minValue = tempFilterState.minHeight,
            maxValue = tempFilterState.maxHeight,
            onMinChange = { tempFilterState = tempFilterState.copy(minHeight = it) },
            onMaxChange = { tempFilterState = tempFilterState.copy(maxHeight = it) }
        )

        // Width filter
        DimensionFilter(
            label = "Ancho",
            minValue = tempFilterState.minWidth,
            maxValue = tempFilterState.maxWidth,
            onMinChange = { tempFilterState = tempFilterState.copy(minWidth = it) },
            onMaxChange = { tempFilterState = tempFilterState.copy(maxWidth = it) }
        )

        // Length filter
        DimensionFilter(
            label = "Largo",
            minValue = tempFilterState.minLength,
            maxValue = tempFilterState.maxLength,
            onMinChange = { tempFilterState = tempFilterState.copy(minLength = it) },
            onMaxChange = { tempFilterState = tempFilterState.copy(maxLength = it) }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    tempFilterState = FilterState()
                    viewModel.clearFilters()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Limpiar")
            }
            Button(
                onClick = {
                    viewModel.updateFilterState(tempFilterState)
                    onDismiss()
                },
                modifier = Modifier.weight(1f)
            ) {
                Text("Aplicar")
            }
        }
    }
}

@Composable
fun DimensionFilter(
    label: String,
    minValue: Float?,
    maxValue: Float?,
    onMinChange: (Float?) -> Unit,
    onMaxChange: (Float?) -> Unit
) {
    var minText by remember(minValue) { mutableStateOf(minValue?.toString() ?: "") }
    var maxText by remember(maxValue) { mutableStateOf(maxValue?.toString() ?: "") }

    fun parseDecimal(text: String): Float? {
        if (text.isBlank()) return null
        return try {
            text.replace(",", ".").toFloatOrNull()
        } catch (e: Exception) {
            null
        }
    }

    fun isValidDecimalInput(text: String): Boolean {
        if (text.isEmpty()) return true

        val regex = Regex("^\\d*[.,]?\\d*$")
        return regex.matches(text) && text.count { it == ',' || it == '.' } <= 1
    }

    Column(
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Min value
            BasicTextField(
                value = minText,
                onValueChange = { newValue ->
                    if (isValidDecimalInput(newValue)) {
                        minText = newValue
                        onMinChange(parseDecimal(newValue))
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (minText.isEmpty()) {
                            Text("Mín", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                        innerTextField()
                    }
                }
            )
            
            Text(
                text = "-",
                modifier = Modifier.align(Alignment.CenterVertically)
            )
            
            // Max value
            BasicTextField(
                value = maxText,
                onValueChange = { newValue ->
                    if (isValidDecimalInput(newValue)) {
                        maxText = newValue
                        onMaxChange(parseDecimal(newValue))
                    }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (maxText.isEmpty()) {
                            Text("Máx", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
fun FurnitureCard(
    item: FurnitureItem,
    onItemClick: () -> Unit,
    onAddClick: () -> Unit,
    isList: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isList) Modifier.height(140.dp) else Modifier.aspectRatio(0.75f))
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Icono de añadir arriba a la izquierda
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp, bottom = 16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Añadir",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onAddClick() }
                )
            }
            // Contenido del card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp, start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Imagen del mueble
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(File(item.imagePath))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen del mueble",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(if (isList) 0.4f else 1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = CorporatePurple
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Image,
                                contentDescription = "Error al cargar imagen",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Titulo y descripción
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    maxLines = 1
                )

                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF222222),
                    maxLines = if (isList) 1 else 2
                )

                // Información adicional
                Column(
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    // Dimensiones
                    Text(
                        text = "${String.format("%.1f", item.height)}h × ${
                            String.format(
                                "%.1f",
                                item.width
                            )
                        }w × ${String.format("%.1f", item.length)}l m",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )

                    // Superficie
                    Text(
                        text = "Superficie: ${item.superficie.name}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )

                    // Materiales (solo mostrar el primero si hay varios)
                    if (item.materials.isNotEmpty()) {
                        Text(
                            text = "Material: ${item.materials.first()}${if (item.materials.size > 1) " +${item.materials.size - 1}" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = CorporatePurple,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}