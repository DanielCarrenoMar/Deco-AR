package com.app.homear.ui.screens.profile

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.annotation.DrawableRes
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import com.app.homear.R
import com.app.homear.ui.theme.ColorVerde
import com.app.homear.ui.theme.CorporatePurple
import com.app.homear.ui.theme.Typography
import com.app.homear.ui.theme.HomeARTheme

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.app.homear.ui.component.NavBar
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.domain.model.UserModel
import com.app.homear.ui.screens.profile.ProfileViewModel

@Composable
fun ProfileScreen(
    navigateToTutorial: () -> Unit = {},
    navigateToCatalog: () -> Unit = {},
    navigateToCamera: () -> Unit = {},
    navigateToSpaces: () -> Unit = {},
    navigateToLogin: () -> Unit = {},
    navigateToRegister: () -> Unit = {},
    viewModel: ProfileViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()),
        verticalArrangement = Arrangement.SpaceBetween
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
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    if (viewModel.state.isLoading) {
                        // Estado de carga
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = CorporatePurple,
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    } else if (viewModel.state.isAuthenticated && viewModel.state.user != null) {
                        // Usuario autenticado - mostrar perfil
                        AuthenticatedProfileContent(
                            user = viewModel.state.user!!,
                            onEditProfile = { /* TODO: Implementar edición de perfil */ },
                            onLogout = { viewModel.logout() }
                        )
                    } else {
                        // Usuario no autenticado - mostrar opciones de login/registro
                        UnauthenticatedProfileContent(
                            onLogin = navigateToLogin,
                            onRegister = navigateToRegister,
                            errorMessage = viewModel.state.errorMessage,
                            onRetry = { viewModel.refreshUser() }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Mostrar secciones solo si el usuario está autenticado
            if (viewModel.state.isAuthenticated) {
                FurnitureSectionDesignOnly(
                    title = "Proyectos",
                    items = getSampleFurnitureItems()
                )

                Spacer(modifier = Modifier.height(16.dp))

                FurnitureSectionDesignOnly(
                    title = "Lista de Deseos",
                    items = getSampleFurnitureItems()
                )

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        NavBar(
            toCamera = navigateToCamera,
            toTutorial = navigateToTutorial,
            toCatalog = navigateToCatalog,
            toSpaces = navigateToSpaces,
            toConfiguration = null,
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
                )
        )
    }
}

@Composable
fun AuthenticatedProfileContent(
    user: UserModel,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.iconoperfil),
            contentDescription = "Foto de perfil",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = user.name,
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black
        )
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        // Mostrar tipo de usuario
        Text(
            text = "Tipo: ${user.type}",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onEditProfile,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CorporatePurple),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Editar perfil",
                    color = Color.White,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            OutlinedButton(
                onClick = onLogout,
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text(
                    text = "Cerrar sesión",
                    color = CorporatePurple,
                    style = MaterialTheme.typography.labelLarge
                )
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
            painter = painterResource(id = R.drawable.iconoperfil),
            contentDescription = "Icono de perfil",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "¡Bienvenido a HomeAR!",
            style = MaterialTheme.typography.headlineSmall,
            color = Color.Black,
            textAlign = TextAlign.Center
        )

        Text(
            text = "Inicia sesión o regístrate para acceder a todas las funciones",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 8.dp, bottom = 16.dp)
        )

        // Mostrar error si existe
        if (errorMessage != null) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Error al cargar el perfil",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color(0xFFD32F2F)
                    )
                    Text(
                        text = errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFD32F2F),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedButton(
                        onClick = onRetry,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFD32F2F)
                        )
                    ) {
                        Text("Reintentar")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onLogin,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CorporatePurple),
                shape = RoundedCornerShape(24.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Text(
                    text = "Iniciar Sesión",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            OutlinedButton(
                onClick = onRegister,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = CorporatePurple
                )
            ) {
                Text(
                    text = "Registrarse",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun FurnitureSectionDesignOnly(title: String, items: List<FurnitureItem>) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.Black
            )
            Image(
                painter = painterResource(id = R.drawable.iconoflecha),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyRow {
            items(items) { item ->
                FurnitureCardDesignOnly(item = item)
                Spacer(modifier = Modifier.width(12.dp))
            }
        }
    }
}

@Composable
fun FurnitureCardDesignOnly(item: FurnitureItem) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .height(180.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = item.imageResId),
                contentDescription = null,
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Text(
                text = item.type,
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
        }
    }
}

data class FurnitureItem(
    val name: String,
    val type: String,
    @DrawableRes val imageResId: Int
)

fun getSampleFurnitureItems(): List<FurnitureItem> {
    return listOf(
        FurnitureItem("Nombre del Objeto", "Tipo de mueble", R.drawable.sillaxd),
        FurnitureItem("Nombre del Objeto", "Tipo de mueble", R.drawable.sillaxd),
        FurnitureItem("Nombre del Objeto", "Tipo de mueble", R.drawable.sillaxd),
        FurnitureItem("Nombre del Objeto", "Tipo de mueble", R.drawable.sillaxd),
        FurnitureItem("Nombre del Objeto", "Tipo de mueble", R.drawable.sillaxd)
    )
}

@Preview(showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun ProfileScreenPreview() {
    HomeARTheme {
        ProfileScreen(
            navigateToTutorial = {},
            navigateToCatalog = {},
            navigateToCamera = {},
            navigateToSpaces = {},
            navigateToLogin = {},
            navigateToRegister = {}
        )
    }
}