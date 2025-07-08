package com.app.homear.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.homear.ui.component.NavBar

@Composable
fun ProfileScreen(
    navigateToTutorial: () -> Unit,
    navigateToCatalog: () -> Unit,
    navigateToCamera: () -> Unit,
    navigateToSpaces: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(1f)
            .padding(
                top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding(),
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            ),
        verticalArrangement = Arrangement.Bottom
    ) {
        NavBar(
            toCamera = navigateToCamera,
            toTutorial = navigateToTutorial,
            toCatalog = navigateToCatalog,
            toSpaces = navigateToSpaces,
            toConfiguration = null,
        )
    }
}
