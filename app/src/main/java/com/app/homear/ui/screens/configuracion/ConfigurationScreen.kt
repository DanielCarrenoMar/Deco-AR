package com.app.homear.ui.screens.configuracion

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import com.app.homear.R
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.ui.component.NavBard
import com.app.homear.ui.screens.profile.ProfileViewModel

@Composable
fun ConfigurationScreen(
    navigateToTutorial: () -> Unit,
    navigateToCatalog: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToProfile: () -> Unit,
    viewModel: ConfigurationViewModel = hiltViewModel()
) {
    Box(
        contentAlignment = Alignment.TopCenter,
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 20.dp, end = 20.dp)
        ,

    )
    {
        //column para que los elementos se posicionen uno debajo de otro
        Column(verticalArrangement = Arrangement.spacedBy(5.dp))
        {
            //contenedor para el titulo
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp))
            {
                //titulo de la pantalla
                Text(
                    text = "Configuración",
                    fontSize = 45.sp,
                    fontWeight = FontWeight.W800,
                    color = Color("#7E1B75".toColorInt()),
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }

            //Opciones de la pantalla
            OptionConfiguracionCheck("NOTIFICACIONES", R.drawable.icono_toggle_on_fill, R.drawable.icono_toggle_off_fill, true, {}, {})

            //crear lista de opciones dropmenu
            var idiomas = listOf<String>("ESPAÑOL", "INGLES", "FRANCES")
            //IDIOMA:ESPAÑOL es el valor por defecto
            OptionConfiguracionDropMenu("IDIOMA: ESPAÑOL", R.drawable.icono_sms, idiomas)

            //botones normales
            OptionConfiguracion("EDITAR CUENTA", R.drawable.icono_person, {})
            OptionConfiguracion("COMPARTIR APP", R.drawable.icono_share, {})
            OptionConfiguracion("AYUDA", R.drawable.icono_help, {})
            OptionConfiguracion("SOBRE NOSOTROS", R.drawable.icono_error, {})
        }

    }

    // Barra de navegación en la parte inferior
    Column(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f),
        verticalArrangement = Arrangement.Bottom
    )
    {
        NavBard(
            toCamera = navigateToCamera,
            toTutorial = navigateToTutorial,
            toCatalog = navigateToCatalog,
            toProfile = navigateToProfile,
            toConfiguration = null,
        )
    }
}


/**
 * Esta funcion es para celdas estaticas
esta funcion composable define a las celdas de las opciones
@param nombre: define el texto que va a tener la opcion
@param idImagen: recibe la direccion de donde esta almacenado el icono de la opcion,
dicha direccion la retorna el metodo R.drawable.nombreDelarchivo
 @param onclick es la funcion que se ejecuta cuando haces click
 */
@Composable
fun OptionConfiguracion(nombre: String, idImagen: Int, onCLick: () -> Unit)
{
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

    Box(modifier = Modifier.scale(scale).fillMaxWidth())
    {

        Row(
            modifier = Modifier
                //el contenedor va a ocupar todo el ancho disponible
                .fillMaxWidth()
                // estilo del borde, el shape define la forma del borde
                .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(15.dp))
                .padding(10.dp) // ancho del elemento
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                )
                {
                    //codigo que reacione al click
                    onCLick()
                }


        )
        {
            // este es el contenedor de los elementos
            Box(modifier = Modifier.fillMaxWidth())
            {
                Text(
                    text = nombre,
                    modifier = Modifier.align(Alignment.CenterStart),
                    fontSize = 20.sp,
                    color = Color("#3E3E3E".toColorInt()),
                    fontWeight = FontWeight.Bold
                )
                // aqui se carga el icono
                Icon(
                    painter = painterResource(idImagen),
                    contentDescription = "icono Favorito",
                    modifier = Modifier.align(Alignment.CenterEnd).size(40.dp),
                    tint = Color("#7E1B75".toColorInt())

                    )
            }
        }
    }

    //este es el margin entre los elementos
    Spacer(modifier = Modifier.height(10.dp))
}

/**
 *    esta funcion es para celdas tipo CheckBox
 *     esta funcion composable define a las celdas de las opciones
 *     @param nombre: define el texto que va a tener la opcion
 *     @param idImagenOn: recibe la direccion de donde esta almacenado el icono de la opcion,
 *                     dicha direccion la retorna el metodo R.drawable.nombreDelarchivo
 *    @param idImagenOff: recibe la direccion de donde esta almacenado el icono de la opcion,
 *                     dicha direccion la retorna el metodo R.drawable.nombreDelarchivo
 *    @param isActive: define el estado inicial del objeto
 *    @param active: es la funcion que se ejecuta cuando se activa
 *    @param disabled: es la funcion que se ejecuta cuando se desactiva
 *
 */

@Composable
fun OptionConfiguracionCheck(nombre: String, idImagenOn: Int, idImagenOff: Int ,isActive: Boolean, active:() -> Unit, disabled:() -> Unit,)
{
    var colorIcono by remember { mutableStateOf(Color.Magenta) }
    var isActive by remember { mutableStateOf(isActive) }
    var idImagen by remember { mutableStateOf(idImagenOn) }


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


    //verifocar el estado del objeto
    if (isActive)
    {
        colorIcono = Color.Magenta
        idImagen = idImagenOn
    }
    else
    {
        colorIcono = Color.Black
        idImagen = idImagenOff
    }


    Box(
        modifier = Modifier.scale(scale).fillMaxWidth()
    )
    {

        Row(
            modifier = Modifier
                //el contenedor va a ocupar todo el ancho disponible
                .fillMaxWidth()
                // estilo del borde, el shape define la forma del borde
                .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(15.dp))
                .padding(10.dp) // ancho del elemento
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                )
                {
                    if (isActive)
                    {
                        colorIcono = Color.Black
                        isActive = false
                        disabled()
                    }
                    else
                    {
                        colorIcono = Color.Magenta
                        isActive = true
                        active()
                    }
                }


        )
        {
            // este es el contenedor de los elementos
            Box(modifier = Modifier.fillMaxWidth())
            {
                Text(
                    text = nombre,
                    modifier = Modifier.align(Alignment.CenterStart),
                    fontSize = 20.sp,
                    color = Color("#3E3E3E".toColorInt()),
                    fontWeight = FontWeight.Bold
                )
                // aqui se carga el icono
                Icon(
                    painter = painterResource(idImagen),
                    contentDescription = "icono Favorito",
                    modifier = Modifier.align(Alignment.CenterEnd).size(40.dp),
                    tint = Color("#7E1B75".toColorInt()),

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
 *     @param idImagen: recibe la direccion de donde esta almacenado el icono de la opcion,
 *                     dicha direccion la retorna el metodo R.drawable.nombreDelarchivo
 *    @param opciones: son las opciones disponibles en el dropMenu
 */
@Composable
fun OptionConfiguracionDropMenu(nombre: String, idImagen: Int, opciones: List<String>)
{
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

    Box(modifier = Modifier.scale(scale).fillMaxWidth())
    {

        Row(
            modifier = Modifier
                //el contenedor va a ocupar todo el ancho disponible
                .fillMaxWidth()
                // estilo del borde, el shape define la forma del borde
                .border(width = 2.dp, color = Color.Gray, shape = RoundedCornerShape(15.dp))
                .padding(10.dp) // ancho del elemento
                .clickable(
                    interactionSource = interactionSource,
                    indication = null
                )
                {
                    //codigo que reaacione al click
                    expanded = true
                }


        )
        {
            // este es el contenedor de los elementos
            Box(modifier = Modifier.fillMaxWidth())
            {
                Text(
                    text = name,
                    modifier = Modifier.align(Alignment.CenterStart),
                    fontSize = 20.sp,
                    color = Color("#3E3E3E".toColorInt()),
                    fontWeight = FontWeight.Bold
                )
                // aqui se carga el icono
                Icon(
                    painter = painterResource(idImagen),
                    contentDescription = "icono Favorito",
                    modifier = Modifier.align(Alignment.CenterEnd).size(40.dp),
                    tint = Color("#7E1B75".toColorInt()),

                    )
            }
        }

        //logica del dropMenu
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }, // Cerrar al tocar fuera
            modifier = Modifier
                .background(Color.White) // Fondo blanco para el menú
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


//composable para preview de la pantalla
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    ConfigurationScreen(
        navigateToCamera = {},
        navigateToCatalog = {},
        navigateToProfile = {},
        navigateToTutorial = {}
    )
}


