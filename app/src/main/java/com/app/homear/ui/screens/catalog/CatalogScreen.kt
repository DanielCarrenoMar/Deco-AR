package com.app.homear.ui.screens.catalog

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.Surface
import androidx.compose.ui.text.TextStyle

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search

import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.app.homear.ui.component.NavBar
import com.app.homear.ui.theme.CorporatePurple
import com.app.homear.domain.model.Superficie
import com.app.homear.R
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatalogScreen(
    navigateToTutorial: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToSpaces: () -> Unit,
    navigateToConfiguration: () -> Unit,
    navigateToAddProducto: () -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadFurnitureData()
        viewModel.checkIfProvider()
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
                fontSize = 36.sp,
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    lineHeight = 40.sp,
                    color = CorporatePurple,
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(Color(0xFFF2F2F2)),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                    } else {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Buscar",
                            tint = Color.Gray
                        )
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
                        .size(45.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Transparent)
                        .clickable { viewModel.toggleFilters() }
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(
                                if (viewModel.hasActiveFilters())
                                    "file:///android_asset/catalogo/filter-selected.svg"
                                else
                                    "file:///android_asset/catalogo/filter.svg"
                            )
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Filtrar",
                        modifier = Modifier.size(45.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                // Selector de vista a la derecha
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Transparent)
                            .clickable { viewModel.isGridView = true }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(
                                    if (viewModel.isGridView)
                                        "file:///android_asset/catalogo/grid-selected.svg"
                                    else
                                        "file:///android_asset/catalogo/grid.svg"
                                )
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "Vista Grid",
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.Transparent)
                            .clickable { viewModel.isGridView = false }
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(
                                    if (!viewModel.isGridView)
                                        "file:///android_asset/catalogo/list-selected.svg"
                                    else
                                        "file:///android_asset/catalogo/list.svg"
                                )
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "Vista Lista",
                            modifier = Modifier.size(36.dp)
                        )
                    }
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
                toSpaces = navigateToSpaces,
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

        // Modal Vista Mueble
        viewModel.selectedItem?.let { selectedItem ->
            ModalVistaMuebleDynamic(
                isDialogOpen = viewModel.showItemModal,
                onDismiss = { viewModel.closeItemModal() },
                onConfirm = { viewModel.confirmItemAction() },
                nombreObjeto = selectedItem.name,
                tipoObjeto = selectedItem.materials.firstOrNull() ?: "Mueble",
                altoObjeto = "${selectedItem.height}m",
                anchoObjeto = "${selectedItem.width}m",
                profundidadObjeto = "${selectedItem.length}m",
                materialObjeto = selectedItem.materials.joinToString(", "),
                imagePath = selectedItem.imagePath
            )
        }
        
        // Agregado: Botón para agregar producto
        if (viewModel.isProvider) {
            AddButton(
                onClick = navigateToAddProducto,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            )
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
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Filtros",
                style = TextStyle(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp,
                    lineHeight = 28.sp,
                    color = Color.Black
                )
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
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    lineHeight = 24.sp,
                    color = Color.Black
                ),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth()
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
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 8.dp)
        )
        LazyRow(
            modifier = Modifier.fillMaxWidth()
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
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                color = Color.Black
            ),
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
            style = TextStyle(
                fontWeight = FontWeight.Normal,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                color = Color.Black
            ),
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (minText.isEmpty()) {
                            Text(
                                "Mín",
                                color = Color.Gray,
                                style = TextStyle(fontSize = 13.sp, color = Color.Gray)
                            )
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        if (maxText.isEmpty()) {
                            Text(
                                "Máx",
                                color = Color.Gray,
                                style = TextStyle(fontSize = 13.sp, color = Color.Gray)
                            )
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
    val context = LocalContext.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isList) Modifier.height(120.dp) else Modifier.wrapContentHeight())
            .then(
                if (!isList) Modifier.clickable(onClick = onItemClick) else Modifier
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5))
    ) {
        if (isList) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(File(item.imagePath))
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen del mueble",
                    modifier = Modifier
                        .width(90.dp)
                        .height(90.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = CorporatePurple
                            )
                        }
                    },
                    error = {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Image,
                                contentDescription = "Error",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = item.name,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 20.sp
                        )
                    )
                    Text(
                        text = item.materials.firstOrNull() ?: "Tipo de mueble",
                        style = TextStyle(
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { onItemClick() },
                            modifier = Modifier
                                .height(36.dp)
                                .weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color.DarkGray
                            ),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = SolidColor(Color.LightGray)
                            )
                        ) {
                            Text(
                                "Detalles",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Button(
                            onClick = { onAddClick() },
                            modifier = Modifier
                                .height(36.dp)
                                .weight(1f),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = CorporatePurple,
                                contentColor = Color.White
                            )
                        ) {
                            Text(
                                "Agregar",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        } else {
            // Grid view (igual que antes)
            Box(
                modifier = Modifier.fillMaxSize()
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("file:///android_asset/catalogo/add.svg")
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = "Agregar",
                    modifier = Modifier
                        .size(26.dp)
                        .align(Alignment.TopStart)
                        .clickable { onAddClick() }
                        .offset(x = 8.dp, y = 8.dp)
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 40.dp, start = 12.dp, end = 12.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.Top
                ) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(File(item.imagePath))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen del mueble",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .background(Color.LightGray),
                        contentScale = ContentScale.Crop,
                        loading = {
                            Box(
                                modifier = Modifier.fillMaxSize(),
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
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Filled.Image,
                                    contentDescription = "Error",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = item.name,
                        style = TextStyle(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp,
                            lineHeight = 22.sp,
                            color = Color.Black
                        ),
                        maxLines = 2
                    )

                    if (item.materials.isNotEmpty()) {
                        Text(
                            text = " ${item.materials.first()}" +
                                    if (item.materials.size > 1) " +${item.materials.size - 1}" else "",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                                color = Color.Gray
                            ),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

// Composable ModalVistaMuebleDynamic
@Composable
fun ModalVistaMuebleDynamic(
    isDialogOpen: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    nombreObjeto: String,
    tipoObjeto: String,
    altoObjeto: String,
    anchoObjeto: String,
    profundidadObjeto: String,
    materialObjeto: String,
    imagePath: String?
) {
    if (isDialogOpen) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            // Contenedor del diálogo con fondo transparente
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Transparent), // Fondo transparente
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface // Color del contenido
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    // Imagen dinámica del mueble
                    if (imagePath != null && imagePath.isNotBlank()) {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(File(imagePath))
                                .crossfade(true)
                                .build(),
                            contentDescription = "Imagen del objeto",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .align(Alignment.CenterHorizontally)
                                .clip(RoundedCornerShape(10.dp)),
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
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .align(Alignment.CenterHorizontally)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Image,
                                contentDescription = "Sin imagen",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Título / nombre del Modal
                    Text(
                        text = nombreObjeto,
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    // Tipo de mueble
                    Text(
                        text = tipoObjeto,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Contenido del elemento
                    Text(
                        text = "Alto: $altoObjeto",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "Ancho: $anchoObjeto",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "Profundidad: $profundidadObjeto",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "Material: $materialObjeto",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Botón para Cerrar
                        TextButton(
                            onClick = { onDismiss() },
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(Color.LightGray)
                                .width(120.dp)
                                .height(35.dp)
                        ) {
                            Text(
                                text = "Cerrar",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                        }

                        // Botón para Agregar
                        TextButton(
                            onClick = { onConfirm() },
                            modifier = Modifier
                                .clip(RoundedCornerShape(5.dp))
                                .background(CorporatePurple)
                                .width(120.dp)
                                .height(35.dp)
                        ) {
                            Text(
                                text = "Agregar",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FloatingActionButton(
        onClick = onClick,
        containerColor = MaterialTheme.colorScheme.primary,
        modifier = modifier
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "+",
            tint = MaterialTheme.colorScheme.onPrimary
        )
    }
}