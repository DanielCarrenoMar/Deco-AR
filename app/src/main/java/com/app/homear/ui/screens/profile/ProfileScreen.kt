package com.app.homear.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.ui.component.NavBard

@Composable
fun ProfileScreen(
    navigateToTutorial: () -> Unit,
    navigateToCatalog: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToConfiguration: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize().zIndex(1f),
        verticalArrangement = Arrangement.Bottom
    ) {
        NavBard(
            toCamera = navigateToCamera,
            toTutorial = navigateToTutorial,
            toCatalog = navigateToCatalog,
            toProfile = null,
            toConfiguration = navigateToConfiguration,
        )
    }
}