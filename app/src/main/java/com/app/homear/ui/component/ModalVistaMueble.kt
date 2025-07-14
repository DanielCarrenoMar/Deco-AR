package com.app.homear.ui.component

import com.app.homear.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.app.homear.ui.theme.CorporatePurple

/**
 *  @param isDialogoOpen es un booleano que controla si el modal se muestra o no
 *  @param onDismiss es una funcion que se ejecuta cuando se presiona en el boton de cerrar,o
 *          al presionar fuera del modal, normalmente se usa para cerrar el modal ejemplos
 *          onDismiss = {modalOpen = false}
 *  @param onConfirm es una funcion que se ejecuta cuando se presiona el boton de agregar
 *  @param imgObjeto es el id para dibujar la imagen del objeto, se consigue con esta funcion
 *          R.drawable.nombreDelObjeto, ese objeto debe estar en la carpeta drawable, "esta forma
 *          uso puede cambiar"
 *  los demas parametros son los datos a colocar en el modal como nombre, tipo, altura, etc
 */
@Composable
fun ModalVistaMueble(
    isDialogOpen: Boolean,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    nombreObjeto: String,
    tipoObjeto: String,
    altoObjeto: String,
    anchoObjeto: String,
    profundidadObjeto: String,
    materialObjeto: String,
    imgObjeto: Int
) {
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
            ) {
                Column(
                    modifier = Modifier
                        .padding(12.dp)
                ) {
                    Image(
                        painter = painterResource(imgObjeto),
                        contentDescription = "Imagen del objeto",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .align(Alignment.CenterHorizontally)
                            .clip(RoundedCornerShape(10.dp))
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Título / nombre del Modal
                    Text(
                        text = nombreObjeto,
                        style = TextStyle(
                            fontSize = 25.sp,
                            fontWeight = FontWeight.ExtraBold
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    // Tipo de mueble
                    Text(
                        text = tipoObjeto,
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Contenido del elemento
                    Text(
                        text = "Alto: $altoObjeto",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "Ancho: $anchoObjeto",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "Profundidad: $profundidadObjeto",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Text(
                        text = "Material: $materialObjeto",
                        style = TextStyle(
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        modifier = Modifier.align(Alignment.Start)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botones
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        // Botón para Cerrar
                        TextButton(
                            onClick = { onDismiss() },
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.LightGray)
                                .width(120.dp)
                                .height(35.dp)
                                .semantics { contentDescription = "Cerrar modal" }
                        ) {
                            Text(
                                text = "Cerrar",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                            )
                        }

                        // Botón para Agregar
                        TextButton(
                            onClick = {onConfirm()},
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CorporatePurple)
                                .width(120.dp)
                                .height(35.dp)
                                .semantics { contentDescription = "Agregar objeto" }
                        ) {
                            Text(
                                text = "Agregar",
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

//composable para preview del Modal
@Preview(showBackground = true)
@Composable
fun GreetingPreview()
{
    var modalOpen = true
    ModalVistaMueble(
        isDialogOpen = modalOpen,
        onDismiss = {modalOpen = false},
        onConfirm = {modalOpen = true},
        "Mesa",
        "Comedor",
        "1m",
        "2m",
        "0.70m",
        "madera",
        R.drawable.ic_launcher_foreground
    )
}