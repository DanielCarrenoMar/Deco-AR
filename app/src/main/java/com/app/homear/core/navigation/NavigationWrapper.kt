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
import com.app.homear.ui.screens.camera.HomeScreen
import com.app.homear.ui.screens.configuracion.ConfigurationScreenn
import com.app.homear.ui.screens.loading.LoadingScreen
import com.app.homear.ui.screens.login.LoginScreen
import com.app.homear.ui.screens.profile.ProfileScreen
import com.app.homear.ui.screens.register.RegisterScreen
import com.app.homear.ui.screens.request.RequestScreen


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

        composable<Request> {
            RequestScreen()
        }

        composable<Login> {
            LoginScreen()
        }

        composable<Register> {
            RegisterScreen ()
        }

        composable<Camera> {
            HomeScreen{
                navController.navigate(Catalog)
            }
        }

        composable<Catalog> {
            CatalogScreen ({ navController.navigatePop(Camera) })
        }

        composable<Profile> {
            ProfileScreen ()
        }

        composable<Configuration> {
            ConfigurationScreenn ()
        }

    }
}