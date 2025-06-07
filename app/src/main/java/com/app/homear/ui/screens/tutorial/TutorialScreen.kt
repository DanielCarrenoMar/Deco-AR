package com.app.homear.ui.screens.tutorial

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.ui.component.NavBard

@Composable
fun TutorialScreen(
    navigateToCatalog: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToProfile: () -> Unit,
    navigateToConfiguration: () -> Unit,
    viewModel: TutorialViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier.fillMaxSize().zIndex(1f),
        verticalArrangement = Arrangement.Bottom
    ) {
        NavBard(
            toCamera = navigateToCamera,
            toTutorial = null,
            toCatalog = navigateToCatalog,
            toProfile = navigateToProfile,
            toConfiguration = navigateToConfiguration,
        )
    }
}