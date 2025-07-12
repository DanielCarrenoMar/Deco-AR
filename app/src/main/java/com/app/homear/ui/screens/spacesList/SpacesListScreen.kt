package com.app.homear.ui.screens.spaceslist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.app.homear.ui.theme.CorporatePurple
import java.io.File

@Composable
fun SpacesListScreen(
    onBack: () -> Unit = {},
    navigateToSpacesDetails: (Int) -> Unit = {}
) {
    val dummySpaces = listOf(
        Triple(1, "Sala Moderna", "/storage/emulated/0/Pictures/space_1.jpg"),
        Triple(2, "Comedor Minimalista", "/storage/emulated/0/Pictures/space_2.jpg"),
        Triple(3, "Dormitorio Luminoso", "/storage/emulated/0/Pictures/space_3.jpg"),
        Triple(4, "Oficina Compacta", "/storage/emulated/0/Pictures/space_4.jpg"),
        Triple(5, "Balcón Verde", "/storage/emulated/0/Pictures/space_5.jpg"),
        Triple(6, "Estudio Cálido", "/storage/emulated/0/Pictures/space_6.jpg")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.Gray,
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onBack() }
                )
                Text(
                    text = "Lista de Espacios",
                    color = Color.Gray,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(32.dp)) // para balance visual
            }

            Text(
                text = "Editar",
                color = CorporatePurple,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                modifier = Modifier
                    .clickable { /* Acción de editar */ }
                    .padding(vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(dummySpaces) { (id, name, imagePath) ->
                    SpaceCard(
                        name = name,
                        user = "Usuario $id",
                        imagePath = imagePath,
                        navigateToSpacesDetails = { navigateToSpacesDetails(id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SpaceCard(
    name: String,
    user: String,
    imagePath: String?,
    navigateToSpacesDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = navigateToSpacesDetails),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imagePath?.let { File(it) })
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen del espacio",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
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
                                imageVector = Icons.Filled.Image,
                                contentDescription = "Error al cargar imagen",
                                tint = Color.White,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                )

                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "Opciones",
                    tint = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(20.dp)
                        .clickable { /* Acción opciones */ }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF333333),
                    maxLines = 1
                )
                Text(
                    text = user,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray,
                    maxLines = 1
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun SpacesListScreenPreview() {
    SpacesListScreen()
}
