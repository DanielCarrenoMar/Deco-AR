package com.app.homear.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.homear.ui.screens.addProducto.AddProductoScreen
import com.app.homear.ui.screens.catalog.CatalogScreen
import com.app.homear.ui.screens.camera.CameraScreen
import com.app.homear.ui.screens.configuracion.ConfigurationScreen
import com.app.homear.ui.screens.createspace.CreateSpaceScreen
import com.app.homear.ui.screens.editProfile.EditProfileScreen
import com.app.homear.ui.screens.intro.IntroScreen
import com.app.homear.ui.screens.loading.LoadingScreen
import com.app.homear.ui.screens.login.LoginScreen
import com.app.homear.ui.screens.profile.ProfileScreen
import com.app.homear.ui.screens.register.RegisterScreen
import com.app.homear.ui.screens.spaceDetail.SpaceDetailScreen
import com.app.homear.ui.screens.spaces.SpacesScreen
import com.app.homear.ui.screens.spaceslist.SpacesListScreen
import com.app.homear.ui.screens.tutorial.TutorialScreen
import com.app.homear.ui.screens.start.StartScreen

/**
 * Navega a una pantalla borrandola de la pila de pantallas
 */
fun NavController.navigatePop(route: Any) {
    this.navigate(route) {
        popUpTo(route) { inclusive = true }
    }
}

@Composable
fun NavigationWrapper() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Intro, // En que pagina inicia
        enterTransition = { fadeIn(animationSpec = tween(700)) }, // Animacion de entrada
        exitTransition = { fadeOut(animationSpec = tween(700)) }, // Animacion de salida
        popEnterTransition = {fadeIn(animationSpec = tween(0))}, // Animacion cuado se hace un navigatePop
    ) {
        composable<Intro> {
            IntroScreen(
                onNavigatoNext = { navController.navigatePop(Start) }
            )
        }

        composable<Loading> {
            LoadingScreen(
                onNavigateToStart = { navController.navigatePop(Start) }
            )
        }
        composable<Start> {
            StartScreen(
                onNavigateToLogin = { navController.navigatePop(Login) },
                onNavigateToRegister = { navController.navigatePop(Register) }
            )
        }

        composable<Login> {
            LoginScreen(
                onLoginSuccess = { navController.navigatePop(Tutorial) }
            )
        }

        composable<Register> {
            RegisterScreen(
                onNavigateToLogin = { navController.navigatePop(Login) },
                onRegisterSuccess = { navController.navigatePop(Tutorial) }
            )
        }

        composable<Tutorial> {
            TutorialScreen (
                onHowItWorksClick = { /* Aquí puedes navegar a un tutorial o modal */ },
                { navController.navigatePop(Catalog) },
                { navController.navigatePop(Camera) },
                { navController.navigatePop(Spaces) },
                { navController.navigatePop(Configuration) },
            )
        }

        composable<Camera> {
            CameraScreen(
                { navController.navigatePop(Tutorial) },
                { navController.navigate(Catalog) },
                { navController.navigatePop(Spaces) },
                { navController.navigatePop(Configuration) },
                { navController.navigatePop(CreateSpace) },
            )
        }

        composable<Catalog> {
            CatalogScreen (
                navigateToTutorial = { navController.navigatePop(Tutorial) },
                navigateToCamera = { navController.navigatePop(Camera) },
                navigateToSpaces = { navController.navigatePop(Spaces) },
                navigateToConfiguration = { navController.navigatePop(Configuration) },
            )
        }

        composable<Spaces> {
            SpacesScreen (
                { navController.navigatePop(Tutorial) },
                { navController.navigatePop(Catalog) },
                { navController.navigatePop(Camera) },
                { navController.navigatePop(Configuration) },
            )
        }

        composable<Configuration> {
            ConfigurationScreen(
                { navController.navigatePop(Tutorial) },
                { navController.navigatePop(Catalog) },
                { navController.navigatePop(Camera) },
                { navController.navigatePop(Spaces) },
                navigateToProfile = { navController.navigatePop(Profile) },
            )
        }

        composable<Profile> {
            ProfileScreen(
                navigateToTutorial = { navController.navigatePop(Tutorial) },
                navigateToCatalog = { navController.navigatePop(Catalog) },
                navigateToCamera = { navController.navigatePop(Camera) },
                navigateToSpaces = { navController.navigatePop(Spaces) },
                navigateToLogin = { navController.navigatePop(Login) },
                navigateToRegister = { navController.navigatePop(Register) },
                navigateToEditProfile = { navController.navigate(EditProfile) } // Nuevo callback
            )
        }

        composable<CreateSpace>{
            CreateSpaceScreen (
                navigateToCamera = { navController.navigatePop(Camera)}
            )
        }

        composable<SpaceDetail>{
            SpaceDetailScreen (
                onBack = { navController.popBackStack() }
            )
        }
        composable<EditProfile> {
            EditProfileScreen(
                onBack = { navController.popBackStack() } // Regresa a la pantalla anterior (Profile)
            )
        }

        composable<SpacesList> {
            SpacesListScreen(
                onBack = { navController.popBackStack() }, // Regresa a la pantalla anterior (Profile)
               navigateToSpacesDetails = { navController.navigate(SpaceDetail) }
            )
        }
    }
}
