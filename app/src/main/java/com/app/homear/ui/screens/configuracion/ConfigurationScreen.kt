package com.app.homear.ui.screens.configuracion

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.*
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.app.homear.R
import com.app.homear.ui.component.ModalInfo
import com.app.homear.ui.component.NavBar
import com.app.homear.ui.theme.CorporatePurple
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.window.Dialog

@Composable
private fun SignOutConfirmationDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    isLoading: Boolean
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Cerrar sesión",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF8F006D)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "¿Estás seguro que deseas cerrar sesión?",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF8F006D)
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = SolidColor(Color(0xFF8F006D))
                        ),
                        enabled = !isLoading
                    ) {
                        Text(
                            text = "Cancelar",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = onConfirm,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF8F006D),
                            contentColor = Color.White
                        ),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = "Confirmar",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium
                                ),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfigurationScreen(
    navigateToTutorial: () -> Unit,
    navigateToCatalog: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToSpaces: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToProfileProv: () -> Unit,
    navigateToIntro: () -> Unit,
    viewModel: ConfigurationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    //variables que controlan los modales
    var isModalHelpOpen by remember { mutableStateOf(false) }
    var isModalAboutUsOpen by remember { mutableStateOf(false) }

    // Estado para controlar loading de cierre de sesión
    var isSigningOut by remember { mutableStateOf(false) }
    var showSignOutConfirmation by remember { mutableStateOf(false) }

    //informacion de los modales
    val inforHelp = listOf<String>(
        "¡Hola!\n" +
                "\n" +
                "Lamentamos informarte que, por el momento, no contamos con soporte o una implementación específica para ayudas dentro de nuestra plataforma. Estamos trabajando para integrar estas funcionalidades en el futuro y mejorar tu experiencia.\n" +
                "\n" +
                "Agradecemos tu comprensión."
    )

    val infoAboutUs = listOf(
        "Somos un equipo de estudiantes de la Universidad Católica Andrés Bello (UCAB) Guayana, apasionados por la tecnología y cursando la cátedra de Ingeniería de Software. Este proyecto es el resultado de nuestro esfuerzo y aprendizaje en esta materia, donde aplicamos los conocimientos adquiridos para desarrollar soluciones innovadoras.",
        "Tecnologías utilizadas:\n" +
        "• Kotlin y Jetpack Compose para el desarrollo Android\n" +
        "• Firebase para autenticación y base de datos\n" +
        "• ARCore para realidad aumentada\n" +
        "• Material Design 3 para la interfaz de usuario\n" +
        "• Git y GitHub para control de versiones",
        "Proyecto de código abierto\n" +
        "Puedes encontrar el código fuente en GitHub:\n" +
        "https://github.com/DanielCarrenoMar/Deco-AR/tree/dev"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            )
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 20.dp, end = 20.dp),
        ) {
            Column(
                modifier = Modifier
                    .background(Color(0xFFF7F7F7))
                    .padding(horizontal = 18.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Configuración",
                    textAlign = TextAlign.Center,
                    fontSize = 36.sp,
                    color = CorporatePurple,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )

                // Mostrar diferente ProfileCard según el estado de autenticación
                if (viewModel.state.isLoading) {
                    LoadingProfileCard()
                } else if (viewModel.state.isAuthenticated && viewModel.state.user != null) {
                    UserProfileCard(
                        name = viewModel.state.user!!.name,
                        email = viewModel.state.user!!.email,
                        role = viewModel.state.user!!.type,
                        navigateToProfile = { 
                            if (viewModel.state.user!!.type.lowercase() == "provider") {
                                navigateToProfileProv()
                            } else {
                                navigateToProfile()
                            }
                        }
                    )
                } else {
                    UnauthenticatedProfileCard(
                        navigateToProfile = navigateToProfile
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Mostrar opciones solo si el usuario está autenticado
                if (viewModel.state.isAuthenticated) {
                    Text(
                        text = "Cuenta",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp,
                        color = Color(0xFF222222),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    OptionConfiguracion(
                        nombre = "Cambiar contraseña",
                        iconPath = "file:///android_asset/configuracion/lock.svg",
                        onClick = {  }
                    )
                    OptionConfiguracion(
                        nombre = "Cerrar sesión",
                        iconPath = "file:///android_asset/configuracion/logout.svg",
                        onClick = { showSignOutConfirmation = true }
                    )

                    if (showSignOutConfirmation) {
                        SignOutConfirmationDialog(
                            onDismiss = { showSignOutConfirmation = false },
                            onConfirm = {
                                isSigningOut = true
                                viewModel.signOut { success ->
                                    isSigningOut = false
                                    if (success) {
                                        showSignOutConfirmation = false
                                        navigateToIntro()
                                    }
                                }
                            },
                            isLoading = isSigningOut
                        )
                    }

                    if (isSigningOut) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                color = CorporatePurple,
                                modifier = Modifier.size(28.dp),
                                strokeWidth = 3.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Cerrando sesión...", color = CorporatePurple)
                        }
                    }
                    Spacer(modifier = Modifier.height(28.dp))
                }

                Text(
                    text = "Soporte",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF222222),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OptionConfiguracion(
                    nombre = "Ayuda",
                    iconPath = "file:///android_asset/configuracion/help.svg",
                    onClick = { isModalHelpOpen = true }
                )
                //modal de ayuda
                if (isModalHelpOpen) {
                    ModalInfo(
                        title = "Ayuda",
                        info = inforHelp,
                        onDismiss = { isModalHelpOpen = false },
                        githubUrl = null
                    )
                }

                OptionConfiguracion(
                    nombre = "Sobre nosotros",
                    iconPath = "file:///android_asset/configuracion/about.svg",
                    onClick = { isModalAboutUsOpen = true },
                )
                //modal sobre nosotros
                if (isModalAboutUsOpen) {
                    ModalInfo(
                        title = "Sobre nosotros",
                        info = listOf(
                            "Somos un equipo de estudiantes de la Universidad Católica Andrés Bello (UCAB) Guayana, apasionados por la tecnología y cursando la cátedra de Ingeniería de Software. Este proyecto es el resultado de nuestro esfuerzo y aprendizaje en esta materia, donde aplicamos los conocimientos adquiridos para desarrollar soluciones innovadoras.",
                            "Tecnologías utilizadas:\n" +
                            "• Kotlin y Jetpack Compose para el desarrollo Android\n" +
                            "• Firebase para autenticación y base de datos\n" +
                            "• ARCore para realidad aumentada\n" +
                            "• Material Design 3 para la interfaz de usuario\n" +
                            "• Git y GitHub para control de versiones",
                            "¡Proyecto de código abierto!\nExplora nuestro código, contribuye y aprende con nosotros."
                        ),
                        onDismiss = { isModalAboutUsOpen = false },
                        githubUrl = "https://github.com/DanielCarrenoMar/Deco-AR/tree/dev"
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "Zona de Peligro",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF222222),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OptionConfiguracion(
                    nombre = "Borrar Proyectos",
                    iconPath = "file:///android_asset/configuracion/about.svg",
                    onClick = { viewModel.deleteAllProjectandSpaces() }
                )
                Spacer(modifier = Modifier.height(100.dp))
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .zIndex(1f)
        ) {
            NavBar(
                toCamera = navigateToCamera,
                toTutorial = navigateToTutorial,
                toCatalog = navigateToCatalog,
                toSpaces = navigateToSpaces,
                toConfiguration = null,
            )
        }
    }
}

@Composable
fun LoadingProfileCard() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFFE0E0E0)),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = CorporatePurple,
            modifier = Modifier.size(32.dp)
        )
    }
}

@Composable
fun UnauthenticatedProfileCard(
    navigateToProfile: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleUnauthenticatedProfileCard"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0xFF6D6D6D))
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { navigateToProfile() }
            )
            .padding(24.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                painter = painterResource(id = R.drawable.iconoperfil),
                contentDescription = "Imagen de usuario",
                modifier = Modifier
                    .size(56.dp)
                    .background(Color.White, RoundedCornerShape(50))
                    .padding(8.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Iniciar Sesión",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Toca aquí para acceder a tu cuenta",
                fontSize = 14.sp,
                color = Color(0xFFE0E0E0),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun UserProfileCard(
    name: String,
    email: String,
    role: String,
    navigateToProfile: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scaleUserProfileCard"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(CorporatePurple)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { navigateToProfile() }
            )
            .padding(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.iconoperfil),
                    contentDescription = "Imagen de usuario",
                    modifier = Modifier
                        .size(56.dp)
                        .background(Color.White, RoundedCornerShape(50))
                        .padding(8.dp),
                    contentScale = ContentScale.Crop
                )
                Column {
                    Text(
                        text = name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = email,
                        fontSize = 14.sp,
                        color = Color(0xFFE0E0E0)
                    )
                    Text(
                        text = role,
                        fontSize = 12.sp,
                        color = Color(0xFFE0E0E0)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Ir al perfil",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun OptionConfiguracion(nombre: String, iconPath: String, onClick: () -> Unit){
    val context = LocalContext.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scaleOption"
    )

    Box(modifier = Modifier
        .scale(scale)
        .fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color.Gray, RoundedCornerShape(15.dp))
                .padding(10.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    onClick()
                }
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = nombre,
                    modifier = Modifier.align(Alignment.CenterStart),
                    fontSize = 20.sp,
                )
                AsyncImage(
                    model = ImageRequest.Builder(context)
                        .data(iconPath)
                        .decoderFactory(SvgDecoder.Factory())
                        .build(),
                    contentDescription = "icono",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp)
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(10.dp))
}