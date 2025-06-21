package com.app.homear.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.toColorInt


@Composable
        /**
         * @param onclick es la funcion que se realiza al darle click al botom
         * @param active dice que apartado esta activo por defecto, true es para cliente
         * y false para empresa
         * @param opcion1 es el nombre de la opcion 1
         * @param opcion2 es el nombre de la opcion 2
         */
fun ButtonSwitch(
    onClick:()-> Unit,
    active: Boolean,
    opcion1: String,
    opcion2: String
)
{
    // Definición de colores
    val colorOpcion = Color("#009971".toColorInt()) // Verde oscuro
    val colorBoton = Color("#D9D9D9".toColorInt()) // Gris claro
    val colorTextActive = Color("#F2F2F2".toColorInt()) // Gris claro
    val colorTextDesactive = Color("#656565".toColorInt()) // Gris oscuro

    // Estado para determinar la opción activa (Cliente o Empresa)
    var isClientActive by remember { mutableStateOf(active) }

    Box(
        modifier = Modifier
            .fillMaxWidth(0.75f)
            .height(50.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(colorBoton)
            .clickable {
                // Alternar la opción activa
                isClientActive = !isClientActive
                onClick()
            }
    ) {
        // Opción 1
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.55f)
                .clip(RoundedCornerShape(30.dp))
                .background(if (isClientActive) colorOpcion else colorBoton)
                .align(Alignment.CenterStart)
                .zIndex(if (isClientActive) 1f else 0f)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = opcion1,
                color = if (isClientActive) colorTextActive else colorTextDesactive,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Opción 2
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .fillMaxWidth(0.55f)
                .clip(RoundedCornerShape(30.dp))
                .background(if (!isClientActive) colorOpcion else colorBoton)
                .align(Alignment.CenterEnd)
                .zIndex(if (!isClientActive) 1f else 0f)
        ) {
            Text(
                modifier = Modifier.align(Alignment.Center),
                text = opcion2,
                color = if (!isClientActive) colorTextActive else colorTextDesactive,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun Preview() {
    ButtonSwitch({}, true, "Cliente", "Empresa")
}

