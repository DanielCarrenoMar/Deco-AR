package com.app.homear.ui.screens.createspace

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.app.homear.R
import com.app.homear.ui.theme.CorporatePurple
import java.io.File
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CreateSpaceScreen(
    navigateToCamera: () -> Unit,
    navigateToCreateProject: () -> Unit,
    viewModel: CreateSpaceViewModel = hiltViewModel()
) {
    val spaceName = remember { mutableStateOf("") }
    val furnitureList by viewModel.furnitureList

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
                .padding(horizontal = 16.dp, vertical = 16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { navigateToCamera() }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("file:///android_asset/camara/back.svg")
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Back Button",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Crear Espacio",
                    color = Color.DarkGray,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 32.sp,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                val viewModel: CreateSpaceViewModel = hiltViewModel()
                val imagePath by viewModel.imagePath

                coil.compose.SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imagePath?.let { File(it) })
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen del espacio",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(32.dp),
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
                                contentDescription = "Sin imagen",
                                tint = Color.White,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Nombre de Espacio",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
            OutlinedTextField(
                value = spaceName.value,
                onValueChange = { spaceName.value = it },
                placeholder = { Text("Añadir nombre de espacio") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Lista de Muebles",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(furnitureList) { furniture ->
                    FurnitureCard(
                        name = furniture.name,
                        type = furniture.type,
                        description = furniture.description,
                        imagePath = furniture.imagePath,
                        onClick = { /* Acción al pulsar */ }
                    )
                }
            }

            Button(
                onClick = navigateToCreateProject,
                colors = ButtonDefaults.buttonColors(containerColor = CorporatePurple),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text("Siguiente", color = Color.White, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun FurnitureCard(
    name: String,
    type: String,
    description: String?,
    imagePath: String?,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                if (!imagePath.isNullOrEmpty() && File(imagePath).exists()) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(File(imagePath))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen del mueble",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        Icons.Default.Image,
                        contentDescription = "Sin imagen",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                Text(type, fontSize = 12.sp, color = Color.Gray)
                if (!description.isNullOrBlank()) {
                    Text(description, fontSize = 12.sp, color = Color.DarkGray, maxLines = 2)
                }
            }
        }
    }
}

