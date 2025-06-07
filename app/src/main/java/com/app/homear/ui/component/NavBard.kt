package com.app.homear.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

object NavBard {
    data class NavBarItem(
        val title: String,
        val icon: Int,
        val onClick: (() -> Unit)?
    )
}
@Composable
fun NavBard(modifier: Modifier = Modifier,
    toTutorial:(() -> Unit)? = null, toCatalog:(() -> Unit)? = null,
    toCamera:(() -> Unit)? = null, toProfile:(() -> Unit)? = null,
    toConfiguration:(() -> Unit)? = null,
) {
    NavigationBar (
        modifier = modifier.fillMaxWidth()
    ){
        NavigationBarItem(
            selected = toTutorial == null,
            onClick = toTutorial ?: {},
            icon = {},
            label = {Text(text = "Tutorial")}
        )
        NavigationBarItem(
            selected = toCatalog == null,
            onClick = toCatalog ?: {},
            icon = {},
            label = {Text(text = "Catalogo")}
        )
        NavigationBarItem(
            selected = toCamera == null,
            onClick = toCamera ?: {},
            icon = {},
            label = {Text(text = "Camara")}
        )
        NavigationBarItem(
            selected = toProfile == null,
            onClick = toProfile ?: {},
            icon = {},
            label = {Text(text = "Perfil")}
        )
        NavigationBarItem(
            selected = toConfiguration == null,
            onClick = toConfiguration ?: {},
            icon = {},
            label = {Text(text = "Configuración")}
        )
    }
}