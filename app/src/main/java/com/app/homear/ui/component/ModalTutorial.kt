package com.app.homear.ui.component

import com.app.homear.R
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.graphics.toColorInt

/**
 *  @param isDialogoOpen es un booleano que controla si el modal se muestra o no
 *  @param onDismiss es una funcion que se ejecuta cuando se presiona en el boton de cerrar,o
 *          al presionar fuera del modal, normalmente se usa para cerrar el modal ejemplos
 *          onDismiss = {modalOpen = false}
 */

@Composable
fun ModalTutorial(
    isDialogOpen: Boolean,
    onDismiss: () -> Unit,
) {
    val scrollState = rememberScrollState()
    //cargar pasos tutorial
    val pasosTutorial = listOf(
        "Paso 1: asjndkandlawndlawndawnd",
        "Paso 2: aijdoawndoawdniawbflawbfiawbfawl",
        "Paso 3: aijdoawndoawdniawbflawbfiawbfawldawkjdawkdjkawndawnd",
        "Paso 4: aijdoawndoawdniawbflawbfiawbfawlawdoawdnlawdoawndlkawndlwa",
        "Paso 5: aijdoawndoawdniawbflawbfiawbfawland awdkjawdawdjawndlawjndlj",
    )

    if (isDialogOpen) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
        {
            // Contenedor del diálogo con fondo transparente
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Transparent), // Fondo transparente
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface // Color del contenido
            )
            {
                Box(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth(0.8f)
                        .fillMaxHeight(0.6f)

                )
                {
                    Column(
                        modifier = Modifier.fillMaxSize().verticalScroll(scrollState),
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {
                        // Título / nombre del Modal
                        Text(
                            text = "Tutorial",
                            style = TextStyle(
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color("#8F006D".toColorInt()),
                            ),
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        //Pasos de Tutorial

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.Start,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        )
                        {
                            for (item in pasosTutorial) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                )
                                {
                                    // aqui se carga el icono
                                    Icon(
                                        painter = painterResource(R.drawable.icon_brightness_fill),
                                        contentDescription = "icono Favorito",
                                        modifier = Modifier.size(10.dp),
                                        tint = Color("#8F006D".toColorInt()),
                                    )

                                    Spacer(modifier = Modifier.width(5.dp))

                                    // Tipo de mueble
                                    Text(
                                        text = item,
                                        style = TextStyle(
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Normal,
                                            color = Color.Black
                                        )
                                    )
                                }
                            }
                        }

                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Barra de navegación en la parte inferior
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                    )
                    {
                        // Botón para Cerrar
                        TextButton(
                            onClick = { onDismiss() },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                                .fillMaxWidth(0.5f)
                                .height(30.dp),
                            contentPadding = PaddingValues(
                                horizontal = 0.dp,
                                vertical = 0.dp
                            ) // Reducir padding interno
                        ) {
                            Text(
                                text = "Cerrar",
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Gray
                                )
                            )
                        }
                    }

                }

            }
        }
    }
}




//composable para preview de la pantalla
@Preview(showBackground = true)
@Composable
fun GreetingPreview2() {
    ModalTutorial(
        true,{}
    )
}