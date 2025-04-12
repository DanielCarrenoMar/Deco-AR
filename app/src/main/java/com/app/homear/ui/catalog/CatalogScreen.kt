package com.app.homear.ui.catalog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.app.homear.ui.component.NavBard

@Composable
fun CatalogScreen (
    navigateToHome: () -> Unit,
){
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Estas en el Catalogo")
    }
    Column (
        modifier = Modifier.fillMaxSize().zIndex(1f),
        verticalArrangement = Arrangement.Bottom
    ){
        NavBard(
            items = listOf(
                NavBard.NavBarItem(
                    title = "Home",
                    icon = -1,
                    onClick = navigateToHome
                ),
                NavBard.NavBarItem(
                    title = "Catalogo",
                    icon = -1,
                    onClick = null
                )
            )
        )
    }
}