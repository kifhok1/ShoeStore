package com.example.shoestore.data.nav

import OnBoardingScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.shoestore.data.AuthStore
import com.example.shoestore.data.model.OtpType
import com.example.shoestore.data.model.PreferencesManager
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.view.*
import com.example.shoestore.ui.theme.viewModel.CatalogViewModel
import com.example.shoestore.ui.theme.viewModel.CatalogViewModelFactory

sealed class Screen(val route: String) {
    object Onboard : Screen("onboard")
    object Register : Screen("register")
    object SignIn : Screen("sign_in")
    object ForgotPassword : Screen("forgot_password")

    object OtpVerification : Screen("otp_verification/{email}") {
        const val EMAIL_ARG = "email"
        fun route(email: String) = "otp_verification/$email"
    }

    object CreateNewPassword : Screen("create_new_password")
    object Home : Screen("home")
    object Profile : Screen("profile")
    object Catalog : Screen("catalog?title={title}") {
        const val TITLE_ARG = "title"
        fun route(title: String): String = "catalog?title=${android.net.Uri.encode(title)}"
    }
    object Favorite : Screen("favorite")
    object Details : Screen("details?productId={productId}") {
        const val PRODUCT_ID_ARG = "productId"
        fun route(productId: String): String = "details?productId=$productId"
    }
}

@Composable
fun NavigationScreen(
    navController: NavHostController,
    authStore: AuthStore,
    isFirstLaunch: Boolean,
    preferencesManager: PreferencesManager
) {
    val catalogViewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModelFactory(authStore)
    )

    // Определяем стартовый экран на основе условий:
    // 1. Если первый запуск -> Onboarding
    // 2. Если пользователь авторизован -> Home
    // 3. Иначе -> SignIn
    val startDestination = when {
        isFirstLaunch -> Screen.Onboard.route
        authStore.getToken() != null -> Screen.Home.route
        else -> Screen.SignIn.route
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Onboard.route) {
            OnBoardingScreen(
                onFinished = {
                    // Отмечаем, что онбординг пройден
                    preferencesManager.setFirstLaunchCompleted()

                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Onboard.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegistrationScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CustomTheme.colors.block),
                onSignInClick = { navController.navigate(Screen.SignIn.route) },
                onBackClick = { navController.popBackStack() },
                onRegisterSuccess = { email ->
                    navController.navigate(Screen.OtpVerification.route(email))
                }
            )
        }

        composable(Screen.SignIn.route) {
            SignIn(
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onForgotPasswordClick = { navController.navigate(Screen.ForgotPassword.route) },
                onHome = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                },
                onBackClick = { navController.popBackStack() }
            )
        }

        composable(Screen.ForgotPassword.route) {
            ForgotPassword(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CustomTheme.colors.block),
                onBackClick = { navController.popBackStack() },
                onOTPClick = { email ->
                    navController.navigate(Screen.OtpVerification.route(email))
                }
            )
        }

        composable(
            route = Screen.OtpVerification.route,
            arguments = listOf(
                navArgument(Screen.OtpVerification.EMAIL_ARG) {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val email = entry.arguments
                ?.getString(Screen.OtpVerification.EMAIL_ARG)
                .orEmpty()

            Verification(
                modifier = Modifier
                    .fillMaxSize()
                    .background(CustomTheme.colors.block),
                email = email,
                otpType = OtpType.EMAIL,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.CreateNewPassword.route)
                }
            )
        }

        composable(Screen.CreateNewPassword.route) {
            CreateNewPassword(
                email = "user@example.com",
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate(Screen.SignIn.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Screen.Home.route) {
            MainAppScreen(
                rootNavController = navController,
                authStore = authStore
            )
        }

        composable(Screen.Profile.route) {
            ProfileScreen()
        }

        composable(
            route = Screen.Catalog.route,
            arguments = listOf(
                navArgument(Screen.Catalog.TITLE_ARG) {
                    type = NavType.StringType
                    defaultValue = "Все"
                }
            )
        ) { entry ->
            val title = entry.arguments
                ?.getString(Screen.Catalog.TITLE_ARG)
                ?.let { android.net.Uri.decode(it) }
                ?: "Все"

            CatalogScreen(
                viewModel = catalogViewModel,
                initialCategoryTitle = title,
                onBackClick = { navController.popBackStack() },
                onProductClick = { card ->
                    navController.navigate(Screen.Details.route(card.id)) {
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(
            route = Screen.Details.route,
            arguments = listOf(
                navArgument(Screen.Details.PRODUCT_ID_ARG) {
                    type = NavType.StringType
                }
            )
        ) { entry ->
            val productId = entry.arguments
                ?.getString(Screen.Details.PRODUCT_ID_ARG)
                .orEmpty()

            ProductDetailsScreen(
                startProductId = productId,
                viewModel = catalogViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
