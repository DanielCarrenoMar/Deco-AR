package com.app.homear.ui.screens.catalog

import android.content.Context
import android.os.Environment
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.ui.component.NavBard
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import coil.compose.rememberAsyncImagePainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.lazy.items
import com.app.homear.ui.theme.Purple60

/**
 * Guarda un archivo en la carpeta de medios de la aplicación.
 */
fun saveFileToMedia(context: Context, fileName: String, fileContent: ByteArray): String? {
    val mediaDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)?.parentFile?.parentFile
        ?.resolve("media")

    if (mediaDir != null && !mediaDir.exists()) {
        mediaDir.mkdirs() // Crea la carpeta si no existe
    }

    return try {
        val file = File(mediaDir, fileName)
        FileOutputStream(file).use { output ->
            output.write(fileContent)
        }
        file.absolutePath // Retorna la ruta del archivo guardado
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

@Composable
fun CatalogScreen(
    navigateToTutorial: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToConfiguration: () -> Unit,
    viewModel: CatalogViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Título del catálogo
            Text(
                text = "Catálogo",
                color = Purple60,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
                    .wrapContentWidth(Alignment.CenterHorizontally)
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
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/catalogo/search.svg")
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Buscar",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    BasicTextField(
                        value = viewModel.searchQuery,
                        onValueChange = { viewModel.searchQuery = it },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        decorationBox = { innerTextField ->
                            if (viewModel.searchQuery.isEmpty()) {
                                Text("Buscar...", color = Color.Gray)
                            }
                            innerTextField()
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Fila de acciones debajo de la barra de búsqueda
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Icono de filtro a la izquierda
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("file:///android_asset/catalogo/filter.svg")
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = "Filtrar",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFFF2F2F2))
                        .border(1.dp, Color.LightGray, RoundedCornerShape(12.dp))
                        .padding(8.dp)
                )
                Spacer(modifier = Modifier.weight(1f))
                // Selector de vista a la derecha
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val gridIcon = if (viewModel.isGridView) "grid-selected.svg" else "grid.svg"
                    val listIcon = if (!viewModel.isGridView) "list-selected.svg" else "list.svg"
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/catalogo/$gridIcon")
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Vista Grid",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (viewModel.isGridView) Color(0xFFE0E0E0) else Color.Transparent)
                            .clickable { viewModel.isGridView = true }
                            .padding(4.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/catalogo/$listIcon")
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Vista Lista",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!viewModel.isGridView) Color(0xFFE0E0E0) else Color.Transparent)
                            .clickable { viewModel.isGridView = false }
                            .padding(4.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            // Lista o grid de items
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
                            onItemClick = { /* TODO: Implementar navegación a detalle */ },
                            context = context
                        )
                    }
                }
            } else {
                androidx.compose.foundation.lazy.LazyColumn(
                    contentPadding = PaddingValues(bottom = 80.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(viewModel.filteredItems) { item ->
                    FurnitureCard(
                        item = item,
                            onItemClick = { /* TODO: Implementar navegación a detalle */ },
                            context = context,
                            isList = true
                    )
                    }
                }
            }
        }
        // Barra de navegación en la parte inferior
        Column(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(1f),
            verticalArrangement = Arrangement.Bottom
        ) {
            NavBard(
                toCamera = navigateToCamera,
                toTutorial = navigateToTutorial,
                toCatalog = null,
                toProfile = navigateToProfile,
                toConfiguration = navigateToConfiguration,
            )
        }
    }
}

@Composable
fun FurnitureCard(
    item: FurnitureItem,
    onItemClick: () -> Unit,
    context: android.content.Context,
    isList: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .then(if (isList) Modifier.height(120.dp) else Modifier.aspectRatio(0.75f))
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Icono de añadir arriba a la izquierda, alineado al contenido interno
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, top = 12.dp, bottom = 16.dp),
                contentAlignment = Alignment.TopStart
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data("file:///android_asset/catalogo/add.svg")
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = "Añadir",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { /* TODO: Acción de añadir */ }
                )
            }
            // Contenido del card
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp, start = 12.dp, end = 12.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Imagen del modelo (por ahora placeholder)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.LightGray)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}