package com.example.shoestore.ui.theme.view

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shoestore.data.AuthStore
import com.example.shoestore.data.nav.Screen
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.ShoeBottomNavigationBar
import com.example.shoestore.ui.theme.components.TabRoutes
import com.example.shoestore.ui.theme.viewModel.CatalogViewModel
import com.example.shoestore.ui.theme.viewModel.CatalogViewModelFactory

@SuppressLint("ViewModelConstructorInComposable")
@Composable
fun MainAppScreen(
    rootNavController: NavHostController,
    authStore: AuthStore// Корневой контроллер для навигации за пределы табов
) {
    // Локальный контроллер только для переключения нижних табов
    val bottomTabsNavController = rememberNavController()

    val catalogViewModel: CatalogViewModel = viewModel(
        factory = CatalogViewModelFactory(authStore)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.block)
    ) {
        NavHost(
            navController = bottomTabsNavController,
            startDestination = TabRoutes.HOME,
            modifier = Modifier.fillMaxSize()
        ) {
            // --- Главная ---
            composable(TabRoutes.HOME) {
                HomeScreen(
                    onOpenCatalog = { categoryName ->
                        // Используем rootNavController для выхода из табов в общий граф навигации
                        // Метод route(title) был определен в обновленном классе Screen.Catalog
                        rootNavController.navigate(Screen.Catalog.route(categoryName))
                    }
                )
            }

            // --- Избранное ---
            composable(TabRoutes.FAVORITE) {
                FavoriteScreen(
                    viewModel = catalogViewModel,
                    authStore = authStore,
                    onBackClick = {
                        // Логика кнопки "Назад" внутри таба: возвращаемся на Главную
                        bottomTabsNavController.navigate(TabRoutes.HOME) {
                            popUpTo(TabRoutes.HOME) { inclusive = true }
                        }
                    },
                    onProductClick = { productData ->
                        // При клике на товар уходим из табов в корневой граф (экран деталей)
                        // Предполагаем, что у вас есть Screen.Details
                        rootNavController.navigate(Screen.Details.route(productData.id))
                    }
                )
            }

            // --- Корзина ---
            composable(TabRoutes.CART) {
                CartScreen()
            }

            // --- Уведомления/Заказы ---
            composable(TabRoutes.ORDERS) {
                OrdersScreen()
            }

            // --- Профиль ---
            composable(TabRoutes.PROFILE) {
                ProfileScreen()
            }
        }

        // Нижняя навигационная панель
        ShoeBottomNavigationBar(
            navController = bottomTabsNavController,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
