package com.example.shoestore.data.nav

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shoestore.data.model.OtpType
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.view.*

@Composable
fun NavigationScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val startDestination = "sign_in"

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {

        composable("register") {
            RegistrationScreen(
                modifier = Modifier.fillMaxSize().background(color = CustomTheme.colors.block),
                onBackClick = { navController.popBackStack() },
                onSignInClick = { navController.navigate("sign_in") },
                // При успехе регистрации идем на OTP с типом EMAIL
                onRegisterSuccess = { email ->
                    navController.navigate("otp_verification/$email/EMAIL")
                }
            )
        }

        composable("sign_in") {
            SignIn(
                onRegisterClick = { navController.navigate("register") },
                onBackClick = { navController.popBackStack() },
                onForgotPasswordClick = { navController.navigate("forgot_password") },
                onHome = { navController.navigate("home") }
            )
        }

        composable("forgot_password") {
            ForgotPassword(
                modifier = Modifier.fillMaxSize().background(color = CustomTheme.colors.block),
                onBackClick = { navController.popBackStack() },
                // При восстановлении идем на OTP с типом RECOVERY
                onOTPClick = { email ->
                    navController.navigate("otp_verification/$email/RECOVERY")
                }
            )
        }

        // Экран верификации принимает email и тип
        composable("otp_verification/{email}/{type}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            val typeStr = backStackEntry.arguments?.getString("type") ?: "EMAIL"
            val otpType = if (typeStr == "RECOVERY") OtpType.RECOVERY else OtpType.EMAIL

            Verification(
                modifier = Modifier.fillMaxSize().background(color = CustomTheme.colors.block),
                email = email,
                otpType = otpType,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    if (otpType == OtpType.RECOVERY) {
                        // Если восстанавливали пароль -> идем создавать новый
                        navController.navigate("create_new_password/$email")
                    } else {
                        // Если регистрировались -> идем на вход (или сразу home)
                        navController.navigate("sign_in") {
                            popUpTo("register") { inclusive = true }
                        }
                    }
                }
            )
        }

        composable("create_new_password/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""
            CreateNewPassword(
                email = email,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate("home") {
                        popUpTo("sign_in") { inclusive = false } // или очистить стек
                    }
                }
            )
        }
    }
}
