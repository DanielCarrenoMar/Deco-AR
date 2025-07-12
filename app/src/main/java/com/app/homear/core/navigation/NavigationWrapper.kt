package com.app.homear.core.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.homear.domain.model.UserModel
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
import com.app.homear.ui.screens.profileProv.ProfileProvScreen
import com.app.homear.ui.screens.register.RegisterScreen
import com.app.homear.ui.screens.spaceDetail.SpaceDetailScreen
import com.app.homear.ui.screens.projects.ProjectsScreen
import com.app.homear.ui.screens.spaceslist.SpacesListScreen
import com.app.homear.ui.screens.tutorial.TutorialScreen
import com.app.homear.ui.screens.start.StartScreen
import com.app.homear.ui.screens.createProject.CreateProjectScreen
import com.app.homear.ui.screens.profile.ProfileViewModel
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
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val user by profileViewModel.state::user
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
                { 
                    // Guardar el origen antes de navegar a la cámara
                    val context = navController.context
                    val sharedPrefHelper = com.app.homear.core.utils.SharedPreferenceHelper(context)
                    sharedPrefHelper.saveStringData("camera_navigation_origin", "tutorial")
                    navController.navigatePop(Camera) 
                },
                { navController.navigatePop(Project) },
                { navController.navigatePop(Configuration) },
            )
        }

        composable<Camera> {
            CameraScreen(
                { navController.navigatePop(Tutorial) },
                { 
                    // Guardar el origen antes de navegar a la cámara
                    val context = navController.context
                    val sharedPrefHelper = com.app.homear.core.utils.SharedPreferenceHelper(context)
                    sharedPrefHelper.saveStringData("camera_navigation_origin", "catalog")
                    navController.navigate(Catalog) 
                },
                { 
                    // Guardar el origen antes de navegar a la cámara
                    val context = navController.context
                    val sharedPrefHelper = com.app.homear.core.utils.SharedPreferenceHelper(context)
                    sharedPrefHelper.saveStringData("camera_navigation_origin", "spaces")
                    navController.navigatePop(Project) 
                },
                { 
                    // Guardar el origen antes de navegar a la cámara
                    val context = navController.context
                    val sharedPrefHelper = com.app.homear.core.utils.SharedPreferenceHelper(context)
                    sharedPrefHelper.saveStringData("camera_navigation_origin", "configuration")
                    navController.navigatePop(Configuration) 
                },
                { navController.navigatePop(CreateSpace) },
                { navController.navigate(CreateProject) }
            )
        }

        composable<Catalog> {
            CatalogScreen (
                navigateToTutorial = { navController.navigatePop(Tutorial) },
                navigateToCamera = { 
                    // Guardar el origen antes de navegar a la cámara
                    val context = navController.context
                    val sharedPrefHelper = com.app.homear.core.utils.SharedPreferenceHelper(context)
                    sharedPrefHelper.saveStringData("camera_navigation_origin", "catalog")
                    navController.navigatePop(Camera) 
                },
                navigateToSpaces = { navController.navigatePop(Project) },
                navigateToConfiguration = {navController.navigatePop(Configuration)},
                navigateToAddProducto = { navController.navigatePop(AddProduct) },
            )
        }

        composable<Project> {
            ProjectsScreen (
                { navController.navigatePop(Tutorial) },
                { navController.navigatePop(Catalog) },
                { 
                    // Guardar el origen antes de navegar a la cámara
                    val context = navController.context
                    val sharedPrefHelper = com.app.homear.core.utils.SharedPreferenceHelper(context)
                    sharedPrefHelper.saveStringData("camera_navigation_origin", "spaces")
                    navController.navigatePop(Camera) 
                },
                { navController.navigatePop(Configuration) },
                { projectId -> navController.navigate(ProjectDetail(projectId)) },
                { navController.navigate(CreateProject) }
            )
        }

        composable<Configuration> {
            ConfigurationScreen(
                { navController.navigatePop(Tutorial) },
                { navController.navigatePop(Catalog) },
                { 
                    // Guardar el origen antes de navegar a la cámara
                    val context = navController.context
                    val sharedPrefHelper = com.app.homear.core.utils.SharedPreferenceHelper(context)
                    sharedPrefHelper.saveStringData("camera_navigation_origin", "configuration")
                    navController.navigatePop(Camera) 
                },
                { navController.navigatePop(Project) },
                navigateToProfile = { navController.navigatePop(Profile) },
                navigateToProfileProv = { navController.navigatePop(ProfileProv) },
                navigateToIntro = { navController.navigatePop(Intro) },
            )
        }

        composable<Profile> {
            ProfileScreen(
                navigateToTutorial = { navController.navigatePop(Tutorial) },
                navigateToCatalog = { navController.navigatePop(Catalog) },
                { 
                    // Guardar el origen antes de navegar a la cámara
                    val context = navController.context
                    val sharedPrefHelper = com.app.homear.core.utils.SharedPreferenceHelper(context)
                    sharedPrefHelper.saveStringData("camera_navigation_origin", "profile")
                    navController.navigatePop(Camera) 
                },
                navigateToSpaces = { navController.navigatePop(Project) },
                navigateToLogin = { navController.navigatePop(Login) },
                navigateToRegister = { navController.navigatePop(Register) },
                navigateToEditProfile = { navController.navigate(EditProfile) }
            )
        }

        composable<ProfileProv> {
            ProfileProvScreen(
                user = user ?: UserModel.DEFAULT
            )
        }

        composable<CreateSpace>{
            CreateSpaceScreen (
                navigateToCamera = { navController.navigate(Camera)},
                navigateToCreateProject = { navController.popBackStack()}
            )
        }

        composable<SpaceDetail> { backStackEntry ->
            val spaceId = backStackEntry.arguments?.getInt("spaceId") ?: 1
            SpaceDetailScreen(
                spaceId = spaceId,
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
               navigateToSpacesDetails = { spaceId -> navController.navigate(SpaceDetail(spaceId)) }
            )
        }
        composable<AddProduct> {
            AddProductoScreen(
                onCancel = { navController.popBackStack() },
                onSuccess = { navController.navigate(Catalog) },
                navigateToEditProfile = { navController.navigate(Profile) },
                navigateToSpacesList = { navController.navigate(SpacesList) },
                user = user ?: UserModel.DEFAULT // Usar default solo si aún no está cargado el real
            )
        }

        composable<CreateProject> {
            CreateProjectScreen(
                onNavigateBack = { navController.popBackStack() },
                navigateToCamera = { 
                    // Guardar el origen antes de navegar a la cámara
                    val context = navController.context
                    val sharedPrefHelper = com.app.homear.core.utils.SharedPreferenceHelper(context)
                    sharedPrefHelper.saveStringData("camera_navigation_origin", "create_project")
                    navController.navigate(Camera) 
                },
                navigateToSpaces = { navController.navigate(Project) }
            )
        }

        composable<ProjectDetail> { backStackEntry ->
            val projectId = backStackEntry.arguments?.getInt("projectId") ?: 1
            ProjectDetailScreen(
                projectId = projectId,
                onBack = { navController.popBackStack() },
                navigateToSpaceDetail = { spaceId -> navController.navigate(SpaceDetail(spaceId)) }
            )
        }
    }
}