package com.app.homear.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

object NavBar {
    data class NavBarItem(
        val title: String,
        val icon: Int,
        val onClick: (() -> Unit)?
    )
}
@Preview(showBackground = true)
@Composable
fun NavBar(
    modifier: Modifier = Modifier,
    toTutorial: (() -> Unit)? = null,
    toCatalog: (() -> Unit)? = null,
    toCamera: (() -> Unit)? = null,
    toProfile: (() -> Unit)? = null,
    toConfiguration: (() -> Unit)? = null,
) {
    val context = LocalContext.current
    // Determinar cuál está seleccionado
    val selectedIndex = when {
        toTutorial == null -> 0
        toCatalog == null -> 1
        toCamera == null -> 2
        toProfile == null -> 3
        toConfiguration == null -> 4
        else -> -1
    }
    val items = listOf(
        Triple("Inicio", "home", toTutorial),
        Triple("Catálogo", "furniture", toCatalog),
        Triple("Cámara", "camera", toCamera),
        Triple("Usuario", "user", toProfile),
        Triple("Configuración", "settings", toConfiguration)
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEachIndexed { index, (label, iconName, onClick) ->
                val isSelected = index == selectedIndex
                val iconFile = if (isSelected) "$iconName-selected.svg" else "$iconName.svg"
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onClick?.invoke() }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data("file:///android_asset/iconos/navbar/$iconFile")
                            .decoderFactory(SvgDecoder.Factory())
                            .build(),
                        contentDescription = label,
                        modifier = Modifier.size(38.dp)
                    )
                }
            }
        }
    }
}