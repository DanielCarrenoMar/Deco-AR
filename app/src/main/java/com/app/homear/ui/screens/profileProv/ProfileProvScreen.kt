package com.app.homear.ui.screens.profileProv

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.annotation.DrawableRes
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.app.homear.R
import com.app.homear.domain.model.UserModel
import com.app.homear.ui.theme.ColorVerde
import com.app.homear.ui.theme.CorporatePurple
import com.app.homear.ui.theme.HomeARTheme
import com.app.homear.ui.screens.profileProv.ProfileProvedorUiState
import com.app.homear.ui.screens.profileProv.ProfileProvViewModel
import android.content.Context
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import org.json.JSONArray
import java.io.File

@Composable
fun ProfileProvScreen(
    user: UserModel,
    viewModel: ProfileProvViewModel = hiltViewModel()
) {
    LaunchedEffect(user) {
        viewModel.loadProvedorProfile(user)
    }
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    // Filtra muebles para el usuario
    val cardsList = remember(user.email) {
        loadProveedorFurnitureCards(context, user.email)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = androidx.compose.foundation.layout.WindowInsets.statusBars.asPaddingValues()
                    .calculateTopPadding()
            )
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .zIndex(1f)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(ColorVerde)
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-90).dp)
                    .padding(horizontal = 16.dp)
            ) {
                androidx.compose.material3.Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    if (uiState.isLoading) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                color = CorporatePurple,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    } else if (uiState.isAuthenticated) {
                        // Mostrar datos del usuario real
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.iconoperfil),
                                contentDescription = "Foto de perfil de proveedor",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        androidx.compose.material3.MaterialTheme.colorScheme.surface,
                                        CircleShape
                                    ),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            androidx.compose.material3.Text(
                                text = user.name,
                                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                                color = Color.Black
                            )
                            androidx.compose.material3.Text(
                                text = user.email,
                                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                            androidx.compose.material3.Text(
                                text = "Tipo: ${user.type}",
                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    } else {
                        UnauthenticatedProfileContent(
                            onLogin = { /* No navegación */ },
                            onRegister = { /* No navegación */ },
                            errorMessage = uiState.errorMessage,
                            onRetry = { viewModel.refreshUser(user) }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            if (uiState.isAuthenticated) {
                FurnitureSectionDesignOnlyReal(
                    title = "Lista de muebles subidos",
                    items = cardsList
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun UnauthenticatedProfileContent(
    onLogin: () -> Unit,
    onRegister: () -> Unit,
    errorMessage: String?,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.iconoperfil), //
            contentDescription = "Icono de perfil",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        androidx.compose.material3.Text(
            text = "¡Bienvenido a HomeAR!",
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        androidx.compose.material3.Text(
            text = "Inicia sesión o regístrate para acceder a todas las funciones",
            style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        if (errorMessage != null) {
            androidx.compose.material3.Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = androidx.compose.material3.CardDefaults.cardColors(
                    containerColor = Color(
                        0xFFFFEBEE
                    )
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    androidx.compose.material3.Text(
                        text = "Error al cargar el perfil",
                        style = androidx.compose.material3.MaterialTheme.typography.titleSmall,
                        color = Color(0xFFD32F2F)
                    )
                    androidx.compose.material3.Text(
                        text = errorMessage,
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD32F2F),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    androidx.compose.material3.OutlinedButton(
                        onClick = onRetry,
                        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFD32F2F)
                        )
                    ) {
                        androidx.compose.material3.Text("Reintentar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            androidx.compose.material3.Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = CorporatePurple),
                shape = RoundedCornerShape(24.dp),
                elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "Iniciar Sesión",
                    color = Color.White,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }

            androidx.compose.material3.OutlinedButton(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp)
            ) {
                androidx.compose.material3.Text(
                    text = "Registrarse",
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}

data class FurnitureItemProveedor(
    val name: String,
    val materials: String,
    val imagePath: String
)

fun loadProveedorFurnitureCards(context: Context, email: String): List<FurnitureItemProveedor> {
    val file = File(context.filesDir, "assets/models.json")
    if (!file.exists()) return emptyList()
    val content = file.readText()
    val array = JSONArray(content)
    val result = mutableListOf<FurnitureItemProveedor>()
    for (i in 0 until array.length()) {
        val obj = array.getJSONObject(i)
        if (obj.optString("proveedorEmail") == email) {
            result.add(
                FurnitureItemProveedor(
                    name = obj.optString("name"),
                    materials = obj.optString("materials"),
                    imagePath = File(
                        context.filesDir,
                        "assets/" + obj.optString("imagePath")
                    ).absolutePath
                )
            )
        }
    }
    return result
}

@Composable
fun FurnitureSectionDesignOnlyReal(title: String, items: List<FurnitureItemProveedor>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        androidx.compose.material3.Text(
            text = title,
            style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(16.dp))
        LazyRow {
            items(items) { item ->
                FurnitureCardDesignOnlyReal(item)
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

@Composable
fun FurnitureCardDesignOnlyReal(item: FurnitureItemProveedor) {
    androidx.compose.material3.Card(
        modifier = Modifier
            .width(150.dp)
            .height(180.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = androidx.compose.material3.CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            AsyncImage(
                model = item.imagePath,
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(8.dp))
            androidx.compose.material3.Text(
                text = item.name,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            androidx.compose.material3.Text(
                text = item.materials,
                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ProfileProvedorScreenPreview() {
    HomeARTheme {
        val previewUser = UserModel(
            name = "Petrolina Sinforosa",
            email = "petrolina2025@gmail.com",
            type = "Proveedor",
            key = "PREVIEW"
        )
        ProfileProvScreen(user = previewUser)
    }
}
