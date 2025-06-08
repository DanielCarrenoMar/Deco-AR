package com.app.homear.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.homear.ui.screens.catalog.CatalogScreen
import com.app.homear.ui.screens.camera.CameraScreen
import com.app.homear.ui.screens.configuracion.ConfigurationScreen
import com.app.homear.ui.screens.loading.LoadingScreen
import com.app.homear.ui.screens.login.LoginScreen
import com.app.homear.ui.screens.profile.ProfileScreen
import com.app.homear.ui.screens.register.RegisterScreen
import com.app.homear.ui.screens.tutorial.TutorialScreen


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
        startDestination = Catalog, // En que pagina inicia
        enterTransition = { fadeIn(animationSpec = tween(700)) }, // Animacion de entrada
        exitTransition = { fadeOut(animationSpec = tween(700)) }, // Animacion de salida
        popEnterTransition = {fadeIn(animationSpec = tween(0))}, // Animacion cuado se hace un navigatePop
    ) {
        composable<Loading> {
            LoadingScreen ()
        }

        composable<Login> {
            LoginScreen(
                { navController.navigatePop(Tutorial) },
            )
        }

        composable<Register> {
            RegisterScreen ()
        }

        composable<Tutorial> {
            TutorialScreen (
                { navController.navigatePop(Catalog) },
                { navController.navigatePop(Camera) },
                { navController.navigatePop(Profile) },
                { navController.navigatePop(Configuration) },
            )
        }

        composable<Camera> {
            CameraScreen(
                { navController.navigatePop(Tutorial) },
                { navController.navigate(Catalog) },
                { navController.navigatePop(Profile) },
                { navController.navigatePop(Configuration) },
            )
        }

        composable<Catalog> {
            CatalogScreen (
                { navController.navigatePop(Tutorial) },
                { navController.navigatePop(Camera) },
                { navController.navigatePop(Profile) },
                { navController.navigatePop(Configuration) },
            )
        }

        composable<Profile> {
            ProfileScreen (
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
                { navController.navigatePop(Profile) },
            )
        }

    }
}