package com.example.shoestore.data.nav

import OnBoardingScreen
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.shoestore.data.model.OtpType
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.view.CreateNewPassword
import com.example.shoestore.ui.theme.view.ForgotPassword
import com.example.shoestore.ui.theme.view.MainAppScreen
import com.example.shoestore.ui.theme.view.RegistrationScreen
import com.example.shoestore.ui.theme.view.SignIn
import com.example.shoestore.ui.theme.view.Verification

object Route {
    const val ONBOARDING = "onboardingscreen"
    const val SIGN_IN = "sign_in"
    const val REGISTER = "register"
    const val FORGOT_PASSWORD = "forgot_password"
    const val HOME = "home"
    const val OTP_VERIFICATION = "otp_verification/{email}/{type}"
    const val CREATE_NEW_PASSWORD = "create_new_password/{email}"

    fun otp(email: String, type: String) = "otp_verification/$email/$type"
    fun createPassword(email: String) = "create_new_password/$email"
}

@Composable
fun NavigationScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    isFirstLaunch: Boolean = false
) {
    val startDestination = if (isFirstLaunch) Route.ONBOARDING else Route.SIGN_IN

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        // --- OnBoarding ---
        composable(Route.ONBOARDING) {
            OnBoardingScreen(
                onFinished = {
                    navController.navigate(Route.REGISTER) {
                        popUpTo(Route.ONBOARDING) { inclusive = true }
                    }
                }
            )
        }

        // --- Registration ---
        composable(Route.REGISTER) {
            RegistrationScreen(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = CustomTheme.colors.block),
                onBackClick = { navController.popBackStack() },
                onSignInClick = {
                    navController.navigate(Route.SIGN_IN) {
                        popUpTo(Route.SIGN_IN) { inclusive = true }
                    }
                },
                // После регистрации идем на OTP с типом EMAIL
                onRegisterSuccess = { email ->
                    navController.navigate(Route.otp(email, "EMAIL"))
                }
            )
        }

        // --- Sign In ---
        composable(Route.SIGN_IN) {
            SignIn(
                onRegisterClick = { navController.navigate(Route.REGISTER) },
                onBackClick = {
                    if (navController.previousBackStackEntry != null) {
                        navController.popBackStack()
                    }
                },
                onForgotPasswordClick = { navController.navigate(Route.FORGOT_PASSWORD) },
                onHome = {
                    navController.navigate(Route.HOME) {
                        popUpTo(Route.SIGN_IN) { inclusive = true }
                    }
                }
            )
        }

        // --- Forgot Password ---
        composable(Route.FORGOT_PASSWORD) {
            ForgotPassword(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = CustomTheme.colors.block),
                onBackClick = { navController.popBackStack() },
                // После ввода почты для восстановления идем на OTP с типом RECOVERY
                onOTPClick = { email ->
                    navController.navigate(Route.otp(email, "RECOVERY"))
                }
            )
        }

        // --- OTP Verification (ГЛАВНЫЕ ИЗМЕНЕНИЯ ЗДЕСЬ) ---
        composable(
            route = Route.OTP_VERIFICATION,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType },
                navArgument("type") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val typeStr = backStackEntry.arguments?.getString("type") ?: "EMAIL"

            // Преобразуем строку в Enum
            val otpType = if (typeStr == "RECOVERY") OtpType.RECOVERY else OtpType.EMAIL

            Verification(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color = CustomTheme.colors.block),
                email = email,
                otpType = otpType,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    // ЛОГИКА РАЗВЕТВЛЕНИЯ
                    if (otpType == OtpType.RECOVERY) {
                        // 1. Если восстанавливаем пароль -> Идем создавать новый пароль
                        navController.navigate(Route.createPassword(email))
                    } else {
                        // 2. Если это регистрация (EMAIL) -> Идем сразу Домой
                        // и очищаем стек, чтобы кнопка "Назад" не вернула на регистрацию
                        navController.navigate(Route.HOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                }
            )
        }

        // --- Create New Password ---
        composable(
            route = Route.CREATE_NEW_PASSWORD,
            arguments = listOf(
                navArgument("email") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            CreateNewPassword(
                email = email,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    // Пароль успешно изменен -> Идем домой, чистим стек
                    navController.navigate(Route.HOME) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        // --- Home ---
        composable(Route.HOME) {
            MainAppScreen()
        }
    }
}
