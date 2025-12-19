package com.example.shoestore.ui.theme.view

// Импорты ваших экранов
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.ShoeBottomNavigationBar
import com.example.shoestore.ui.theme.components.TabRoutes

@Composable
fun MainAppScreen() {
    // Этот NavController управляет ТОЛЬКО переключением вкладок
    val bottomTabsNavController = rememberNavController()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.background)
    ) {
        // 1. Область контента (NavHost вкладок)
        NavHost(
            navController = bottomTabsNavController,
            startDestination = TabRoutes.HOME,
            modifier = Modifier.fillMaxSize()
        ) {
            composable(TabRoutes.HOME) { HomeScreen() }
            composable(TabRoutes.FAVORITE) { FavoriteScreen() }
            composable(TabRoutes.CART) { CartScreen() }
            composable(TabRoutes.ORDERS) { OrdersScreen() }
            composable(TabRoutes.PROFILE) { ProfileScreen() }
        }

        // 2. Само меню (поверх контента снизу)
        ShoeBottomNavigationBar(
            navController = bottomTabsNavController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}