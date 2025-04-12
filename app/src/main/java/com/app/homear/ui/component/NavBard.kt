package com.app.homear.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

object NavBard {
    data class NavBarItem(
        val title: String,
        val icon: Int,
        val onClick: (() -> Unit)?
    )
}
@Composable
fun NavBard(modifier: Modifier = Modifier, items: List<NavBard.NavBarItem>) {
    NavigationBar (
        modifier = modifier.fillMaxWidth()
    ){
        items.forEach { item ->
            NavigationBarItem(
                selected = item.onClick == null,
                onClick = item.onClick ?: {},
                icon = {},
                label = {Text(text = item.title)}
            )
        }
    }
}