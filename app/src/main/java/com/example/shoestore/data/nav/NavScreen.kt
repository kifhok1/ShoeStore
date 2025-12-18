package com.example.shoestore.data.nav

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.view.CreateNewPassword
import com.example.shoestore.ui.theme.view.ForgotPassword
import com.example.shoestore.ui.theme.view.Home
import com.example.shoestore.ui.theme.view.RegistrationScreen
import com.example.shoestore.ui.theme.view.SignIn
import com.example.shoestore.ui.theme.view.Verification

@Composable
fun NavigationScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier // 1. Add modifier parameter here
) {
    NavHost(
        navController = navController,
        startDestination = "register",
        modifier = modifier // 2. Apply it to the NavHost
    ){
        composable("register") {
            RegistrationScreen(
                modifier = Modifier.fillMaxSize().background(color = CustomTheme.colors.block),
                onBackClick = { navController.popBackStack() },
                onSignInClick = { navController.navigate("sign_in") }
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
                onOTPClick = { email ->
                    navController.navigate("otp_verification/$email")
                }
            )
        }

        composable("otp_verification/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            Verification(
                modifier = Modifier.fillMaxSize().background(color = CustomTheme.colors.block),
                email = email,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    // 3. FIX: Pass the email to the next screen so it can be retrieved
                    navController.navigate("create_new_password/$email")
                }
            )
        }

        // 4. FIX: Update route to accept {email} argument
        composable("create_new_password/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email") ?: ""

            CreateNewPassword(
                email = email,
                onBackClick = { navController.popBackStack() },
                onSuccess = {
                    navController.navigate("home")
                }
            )
        }

        composable("home") {
            Home()
        }
    }
}
