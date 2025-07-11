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
import com.app.homear.ui.screens.createSpace.CreateSpaceScreen
import com.app.homear.ui.screens.editProfile.EditProfileScreen
import com.app.homear.ui.screens.intro.IntroScreen
import com.app.homear.ui.screens.loading.LoadingScreen
import com.app.homear.ui.screens.login.LoginScreen
import com.app.homear.ui.screens.profile.ProfileScreen
import com.app.homear.ui.screens.register.RegisterScreen
import com.app.homear.ui.screens.spaceDetail.SpaceDetailScreen
import com.app.homear.ui.screens.projects.ProjectsScreen
import com.app.homear.ui.screens.spaceslist.SpacesListScreen
import com.app.homear.ui.screens.tutorial.TutorialScreen
import com.app.homear.ui.screens.start.StartScreen
import com.app.homear.ui.screens.createProject.CreateProjectScreen
import com.app.homear.ui.screens.projectDetail.ProjectDetailScreen

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
                onNavigateNext = { navController.navigatePop(Loading) },
                onNavigateTutorial = {navController.navigatePop(Tutorial)}
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
                { navController.navigatePop(Project) },
                { navController.navigatePop(Configuration) },
            )
        }

        composable<Camera> {
            CameraScreen(
                { navController.navigatePop(Tutorial) },
                { navController.navigate(Catalog) },
                { navController.navigatePop(Project) },
                { navController.navigatePop(Configuration) },
                { navController.navigatePop(CreateSpace) },
            )
        }

        composable<Catalog> {
            CatalogScreen (
                navigateToTutorial = { navController.navigatePop(Tutorial) },
                navigateToCamera = { navController.navigatePop(Camera) },
                navigateToSpaces = { navController.navigatePop(Project) },
                navigateToConfiguration = {navController.navigatePop(Configuration)},
                navigateToAddProducto = { navController.navigatePop(AddProduct) },
            )
        }

        composable<Project> {
            ProjectsScreen (
                { navController.navigatePop(Tutorial) },
                { navController.navigatePop(Catalog) },
                { navController.navigatePop(Camera) },
                { navController.navigatePop(Configuration) },
                { projectId -> navController.navigate(ProjectDetail(projectId)) },
                { navController.navigate(CreateProject) }
            )
        }

        composable<Configuration> {
            ConfigurationScreen(
                { navController.navigatePop(Tutorial) },
                { navController.navigatePop(Catalog) },
                { navController.navigatePop(Camera) },
                { navController.navigatePop(Project) },
                navigateToProfile = { navController.navigatePop(Profile) },
                navigateToIntro = { navController.navigatePop(Intro) },
            )
        }

        composable<Profile> {
            ProfileScreen(
                navigateToTutorial = { navController.navigatePop(Tutorial) },
                navigateToCatalog = { navController.navigatePop(Catalog) },
                navigateToCamera = { navController.navigatePop(Camera) },
                navigateToSpaces = { navController.navigatePop(Project) },
                navigateToLogin = { navController.navigatePop(Login) },
                navigateToRegister = { navController.navigatePop(Register) },
                navigateToEditProfile = { navController.navigate(EditProfile) } // Nuevo callback
            )
        }

        composable<CreateSpace>{
            CreateSpaceScreen (
                navigateToCamera = { navController.navigate(Camera)},
                navigateToCreateProject = { navController.popBackStack()}
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
        composable<AddProduct>        {
            AddProductoScreen(
                onCancel = { navController.popBackStack() } ,// Regresa a la pantalla anterior (Profile)
                onSuccess = { navController.navigate(Catalog) },
                navigateToEditProfile = { navController.navigate(Profile) },
                navigateToSpacesList = { navController.navigate(SpacesList) }
            )
        }

        composable<CreateProject> {
            CreateProjectScreen(
                onNavigateBack = { navController.popBackStack() },
                navigateToCamera = { navController.navigate(Camera) },
                navigateToSpaces = { navController.navigate(Project) }
            )
        }

        composable<ProjectDetail> { backStackEntry ->
            val projectId = backStackEntry.arguments?.getInt("projectId") ?: 1
            ProjectDetailScreen(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                navigateToSpaceDetail = { spaceId -> navController.navigate(SpaceDetail) }
            )
        }
    }
}