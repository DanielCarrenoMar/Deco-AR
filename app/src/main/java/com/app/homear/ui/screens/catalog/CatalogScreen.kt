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
            Text(
                text = "Catálogo de Productos",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 80.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(viewModel.furnitureItems) { item ->
                    FurnitureCard(
                        item = item,
                        onItemClick = { /* TODO: Implementar navegación a detalle */ }
                    )
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
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f)
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Placeholder para la imagen del modelo 3D
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

            Spacer(modifier = Modifier.height(8.dp))

            // Colores disponibles
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                item.colors.forEach { colorHex ->
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(android.graphics.Color.parseColor(colorHex)))
                            .border(
                                width = 1.dp,
                                color = Color.LightGray,
                                shape = RoundedCornerShape(4.dp)
                            )
                    )
                }
            }
        }
    }
}