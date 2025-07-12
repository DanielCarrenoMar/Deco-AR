package com.app.homear.ui.screens.projectDetail

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
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.app.homear.ui.theme.CorporatePurple
import java.io.File
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.homear.domain.model.SpaceModel
import com.app.homear.domain.usecase.space.GetSpacesByProjectIdUseCase
import com.app.homear.domain.usecase.proyect.GetProjectByIdUseCase
import com.app.homear.domain.model.ProjectModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProjectDetailViewModel @Inject constructor(
    private val getSpacesByProjectIdUseCase: GetSpacesByProjectIdUseCase,
    private val getProjectByIdUseCase: GetProjectByIdUseCase
) : ViewModel() {
    private val _spaces = MutableLiveData<List<SpaceModel>>(emptyList())
    val spaces: LiveData<List<SpaceModel>> = _spaces
    
    private val _project = MutableLiveData<ProjectModel?>(null)
    val project: LiveData<ProjectModel?> = _project

    fun loadProject(projectId: Int) {
        viewModelScope.launch {
            getProjectByIdUseCase(projectId).collect { resource ->
                if (resource is com.app.homear.domain.model.Resource.Success) {
                    _project.value = resource.data
                }
            }
        }
    }

    fun loadSpaces(projectId: Int) {
        viewModelScope.launch {
            getSpacesByProjectIdUseCase(projectId).collect { resource ->
                if (resource is com.app.homear.domain.model.Resource.Success) {
                    _spaces.value = resource.data ?: emptyList()
                }
            }
        }
    }
}

@Composable
fun ProjectDetailScreen(
    projectId: Int,
    onBack: () -> Unit,
    navigateToSpaceDetail: (Int) -> Unit,
    viewModel: ProjectDetailViewModel = hiltViewModel()
) {
    val spaces by viewModel.spaces.observeAsState(emptyList())
    val project by viewModel.project.observeAsState(null)
    
    androidx.compose.runtime.LaunchedEffect(projectId) {
        viewModel.loadProject(projectId)
        viewModel.loadSpaces(projectId)
    }

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
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onBack() }
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data("file:///android_asset/camara/back.svg")
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = "Back Button",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(4.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = project?.name ?: "Cargando...",
                        color = Color.DarkGray,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        textAlign = TextAlign.End
                    )
                    Text(
                        text = "por ${project?.idUser ?: "Cargando..."}",
                        color = Color.Gray,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        textAlign = TextAlign.End
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Imagen del proyecto
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                if (project?.imagePath?.isNotEmpty() == true) {
                    SubcomposeAsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(File(project!!.imagePath))
                            .crossfade(true)
                            .build(),
                        contentDescription = "Imagen del proyecto",
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
                                    contentDescription = "Error al cargar imagen",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    )
                } else {
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Color(0xFFE0E0E0))
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Lista de Espacios",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(1),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(spaces) { space ->
                    SpaceCard(
                        name = space.name,
                        furnitureCount = "${space.description}",
                        imagePath = space.imagePath,
                        onClick = { navigateToSpaceDetail(space.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun SpaceCard(
    name: String,
    furnitureCount: String,
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
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Card(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(8.dp)),
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(imagePath?.let { File(it) })
                        .crossfade(true)
                        .build(),
                    contentDescription = "Imagen del espacio",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
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
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.LightGray),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.Image,
                                contentDescription = "Error al cargar imagen",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(name, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.DarkGray)
                Text(furnitureCount, fontSize = 12.sp, color = Color.Gray)
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProjectDetailScreenPreview() {
    ProjectDetailScreen(
        projectId = 1, // Provide a dummy projectId for preview
        onBack = {},
        navigateToSpaceDetail = {}
    )
}
