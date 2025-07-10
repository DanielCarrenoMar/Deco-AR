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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
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
import com.app.homear.ui.theme.CorporatePurple

/**
 *  @param isDialogoOpen es un booleano que controla si el modal se muestra o no
 *  @param onDismiss es una funcion que se ejecuta cuando se presiona en el boton de cerrar,o
 *          al presionar fuera del modal, normalmente se usa para cerrar el modal ejemplos
 *          onDismiss = {modalOpen = false}
 * @param informacion es una lista de strings para cargar la informacion en el modal
 * @param isList indica si quieres que aparezca el icono en cada elemento de la lista o no
 */

@Composable
fun ModalInfo(
    isDialogOpen: Boolean,
    onDismiss: () -> Unit,
    titulo: String,
    informacion: List<String>,
    isList: Boolean
) {
    val scrollState = rememberScrollState()

    if (isDialogOpen) {
        Dialog(
            onDismissRequest = { onDismiss() },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
        {
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Transparent),
                shape = RoundedCornerShape(14.dp),
                color = Color.White
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                        .fillMaxWidth()
                        .then(
                            if (isList) Modifier.fillMaxHeight(0.7f)
                            else Modifier.wrapContentHeight()
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Título
                    Text(
                        text = titulo,
                        style = TextStyle(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color("#8F006D".toColorInt()),
                        ),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Contenido scrollable si es lista
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isList) Modifier
                                    .weight(1f)
                                    .verticalScroll(rememberScrollState())
                                else Modifier.wrapContentHeight()
                            ),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        informacion.forEach { item ->
                            if (isList) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        painter = painterResource(R.drawable.icon_brightness_fill),
                                        contentDescription = "icono",
                                        modifier = Modifier.size(10.dp),
                                        tint = Color("#8F006D".toColorInt()),
                                    )
                                    Spacer(modifier = Modifier.width(5.dp))
                                    Text(
                                        text = item,
                                        fontSize = 16.sp,
                                        color = Color.Black
                                    )
                                }
                            } else {
                                Text(
                                    text = item,
                                    fontSize = 16.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Botón Entendido
                    TextButton(
                        onClick = { onDismiss() },
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(CorporatePurple)
                            .fillMaxWidth(0.5f)
                            .height(40.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = "Entendido",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
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
    ModalInfo(
        true,{}, "Tutorial",emptyList(),true
    )
}