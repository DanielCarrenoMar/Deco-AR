package com.app.homear.ui.screens.configuracion

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.ui.component.NavBar
import com.app.homear.ui.screens.profile.ProfileViewModel
import com.app.homear.R
import kotlinx.coroutines.launch
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

@Composable
fun ConfigurationScreen(
    navigateToTutorial: () -> Unit,
    navigateToCatalog: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToProfile: () -> Unit,
    viewModel: ConfigurationViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    
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
                Spacer(modifier = Modifier.height(16.dp)) // Igual que catálogo
                
                // Título centrado como en el catálogo
                Text(
                    text = "Configuración",
                    color = com.app.homear.ui.theme.CorporatePurple,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .wrapContentWidth(Alignment.CenterHorizontally)
                )
                
                // Card de usuario elegante
                UserProfileCard(
                    name = "Juan Pérez",
                    email = "juan.perez@gmail.com",
                    role = "Usuario"
                )
                Spacer(modifier = Modifier.height(32.dp))
                
                // Agrupación: Preferencias
                Text(
                    text = "Preferencias",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF222222),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OptionConfiguracionCheck(
                    nombre = "Notificaciones",
                    iconPath = "file:///android_asset/configuracion/toggle.svg",
                    active = true
                )
                OptionConfiguracionDropMenu(
                    nombre = "Idioma",
                    iconPath = "file:///android_asset/configuracion/languaje.svg",
                    opciones = listOf("Español", "Inglés")
                )
                Spacer(modifier = Modifier.height(28.dp))
                
                // Agrupación: Cuenta
                Text(
                    text = "Cuenta",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF222222),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OptionConfiguracion(
                    nombre = "Cambiar contraseña",
                    iconPath = "file:///android_asset/configuracion/lock.svg"
                )
                OptionConfiguracion(
                    nombre = "Cerrar sesión",
                    iconPath = "file:///android_asset/configuracion/logout.svg"
                )
                Spacer(modifier = Modifier.height(28.dp))
                
                // Agrupación: Soporte
                Text(
                    text = "Soporte",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp,
                    color = Color(0xFF222222),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OptionConfiguracion(
                    nombre = "Ayuda",
                    iconPath = "file:///android_asset/configuracion/help.svg"
                )
                OptionConfiguracion(
                    nombre = "Sobre nosotros",
                    iconPath = "file:///android_asset/configuracion/about.svg"
                )
                Spacer(modifier = Modifier.height(100.dp)) // Espacio para el navbar
            }
        }

        // Barra de navegación en la parte inferior
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
                toProfile = navigateToProfile,
                toConfiguration = null,
            )
        }
    }
}

/**
 * Esta funcion es para celdas estaticas
esta funcion composable define a las celdas de las opciones
@param nombre: define el texto que va a tener la opcion
@param iconPath: recibe la ruta del icono SVG en assets
 */
@Composable
fun OptionConfiguracion(nombre: String, iconPath: String) {
    val context = LocalContext.current
    // Configuración de la animación de escala
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // Escala 90% cuando se presiona
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scaleAnimation"
    )
    Box(modifier = Modifier.scale(scale).fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(15.dp))
                .padding(10.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    //codigo que reaacione al click
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

/**
 *    esta funcion es para celdas tipo CheckBox
 *     esta funcion composable define a las celdas de las opciones
 *     @param nombre: define el texto que va a tener la opcion
 *     @param iconPath: recibe la ruta del icono SVG en assets
 *    @param active: define el estado inicial del objeto
 */

@Composable
fun OptionConfiguracionCheck(nombre: String, iconPath: String, active: Boolean)
{
    val context = LocalContext.current
    var isActive by remember { mutableStateOf(active) }
    val scope = rememberCoroutineScope()

    // Configuración de la animación de escala
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // Escala 90% cuando se presiona
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scaleAnimation"
    )

    Box(
        modifier = Modifier
            .scale(scale)
            .fillMaxWidth()
    )
    {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 2.dp, 
                    color = if (isActive) com.app.homear.ui.theme.CorporatePurple else Color.Gray, 
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(10.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                ) {
                    scope.launch {
                        isActive = !isActive
                    }
                }
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = nombre,
                    modifier = Modifier.align(Alignment.CenterStart),
                    fontSize = 20.sp,
                )
                // Icono toggle dinámico
                androidx.compose.foundation.Image(
                    painter = painterResource(id = if (isActive) com.app.homear.R.drawable.icono_toggle_on else com.app.homear.R.drawable.icono_toggle_off),
                    contentDescription = "toggle",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(40.dp),
                    colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(
                        if (isActive) com.app.homear.ui.theme.CorporatePurple else Color.Gray
                    )
                )
            }
        }
    }

    //este es el margin entre los elementos
    Spacer(modifier = Modifier.height(10.dp))
}


/**
 *    esta funcion es para celdas tipo DropMenu
 *     esta funcion composable define a las celdas de las opciones
 *     @param nombre: define el texto que va a tener la opcion
 *     @param iconPath: recibe la ruta del icono SVG en assets
 *    @param opciones: son las opciones disponibles en el dropMenu
 */
@Composable
fun OptionConfiguracionDropMenu(nombre: String, iconPath: String, opciones: List<String>)
{
    val context = LocalContext.current
    
    // Configuración de la animación de escala
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f, // Escala 90% cuando se presiona
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioLowBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "scaleAnimation"
    )

    //variable para cambiar nombre
    var name by remember { mutableStateOf(nombre) }
    //variables para el dropMenu
    var expanded by remember { mutableStateOf(false) } // Estado para controlar el DropdownMenu
    //lista de opciones para el dropMenu
    val menuItems = opciones

    Box(modifier = Modifier
        .scale(scale)
        .fillMaxWidth())
    {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(15.dp))
                .padding(10.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                )
                {
                    expanded = true
                }


        )
        {
            Box(modifier = Modifier.fillMaxWidth())
            {
                Text(
                    text = name,
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

        //logica del dropMenu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .background(Color.White)
        )
        {
            menuItems.forEach {
                    item ->
                DropdownMenuItem(
                    text = {Text(item)},
                    onClick = {
                        name = "IDIOMA: $item"
                        expanded = false
                    }

                )
            }
        }
    }

    //este es el margin entre los elementos
    Spacer(modifier = Modifier.height(10.dp))
}

@Composable
fun UserProfileCard(
    name: String = "Juan Pérez",
    email: String = "juan.perez@gmail.com",
    role: String = "Usuario"
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(com.app.homear.ui.theme.CorporatePurple, RoundedCornerShape(18.dp))
            .padding(18.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Imagen de usuario de ejemplo
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = "Imagen de usuario",
                modifier = Modifier
                    .size(64.dp)
                    .background(Color.White, RoundedCornerShape(50))
                    .padding(4.dp),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(18.dp))
            Column {
                Text(
                    text = name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = email,
                    fontSize = 15.sp,
                    color = Color(0xFFE0E0E0)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = role,
                        fontSize = 13.sp,
                        color = Color(0xFF6D6D6D),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
