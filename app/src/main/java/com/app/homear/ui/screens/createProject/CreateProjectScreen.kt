package com.app.homear.ui.screens.createProject

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.app.homear.ui.theme.CorporatePurple
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.ui.screens.createProject.Espacio
import androidx.compose.ui.layout.ContentScale
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateProjectScreen(
    onNavigateBack: () -> Unit = {},
    navigateToCamera: () -> Unit = {},
    navigateToSpaces: () -> Unit = {},
    viewModel: CreateProjectViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    // Efecto para manejar la navegación cuando se crea el proyecto
    LaunchedEffect(viewModel.isProjectCreated.value) {
        if (viewModel.isProjectCreated.value) {
            navigateToSpaces()
            viewModel.resetState(context)
        }
    }
    
    // Efecto para restaurar el estado cuando se regresa de la cámara
    LaunchedEffect(Unit) {
        viewModel.restoreProjectState(context)
        viewModel.restoreSpacesList(context)
        viewModel.clearCreationState()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding())
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Encabezado
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateBack() }
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
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Crear Proyecto",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.DarkGray,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.End
                )
            }
        }

        item {
            Text(
                text = "Nombre de Proyecto",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )

            OutlinedTextField(
                value = viewModel.projectName.value,
                onValueChange = { viewModel.updateProjectName(context,it) },
                placeholder = { Text(text = "Ingresa el nombre del proyecto") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp)
            )
        }
        
        item {
            Text(
                text = "Descripción del Proyecto",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )

            OutlinedTextField(
                value = viewModel.projectDescription.value,
                onValueChange = { viewModel.updateProjectDescription(context,it) },
                placeholder = { Text(text = "Describe tu proyecto (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                shape = RoundedCornerShape(8.dp),
                minLines = 3,
                maxLines = 5
            )
        }

        item {
            Text(
                text = "Lista de Espacios",
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
        
        if (viewModel.spacesList.value.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Image,
                            contentDescription = "Sin espacios",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hay espacios agregados",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Usa el botón 'Agregar espacio' para comenzar",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        items(viewModel.spacesList.value) { espacio ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    if (espacio.imagePath != null) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(File(espacio.imagePath))
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "Imagen espacio",
                            modifier = Modifier.size(60.dp),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data("file:///android_asset/icons/gallery.svg")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = "Imagen espacio",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = espacio.nombre,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "Cantidad de muebles: ${espacio.cantidadMuebles}",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }

        item {
            Button(
                onClick = navigateToCamera,
                colors = ButtonDefaults.buttonColors(containerColor = CorporatePurple),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Agregar espacio", color = Color.White, fontSize = 16.sp)
            }
        }

        item {
            // Mostrar mensaje de error si existe
            viewModel.errorMessage.value?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
        
        item {
            Button(
                onClick = { viewModel.createProject(context) },
                colors = ButtonDefaults.buttonColors(containerColor = CorporatePurple),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                enabled = !viewModel.isLoading.value
            ) {
                if (viewModel.isLoading.value) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = "Crear Proyecto",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CreateProjectPreview() {
    CreateProjectScreen()
}
