package com.example.shoestore

import OnBoardingScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.rememberNavController
import com.example.shoestore.data.model.PreferencesManager
import com.example.shoestore.data.nav.NavigationScreen
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.ShoeStoreTheme
import com.example.shoestore.ui.theme.view.Verification

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val preferencesManager = PreferencesManager(this)
        val isFirstLaunch = preferencesManager.isFirstLaunch()

        // Сохраняем флаг после первого запуска
        if (isFirstLaunch) {
            preferencesManager.setFirstLaunchCompleted()
        }

        // 1. Убираем рамки (делаем приложение на весь экран)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // 2. Прячем системные панели (Статус бар сверху и Навигацию снизу)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE // Появятся только если свайпнуть от края
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars()) // Скрыть всё

        setContent {
            val navController = rememberNavController()
            ShoeStoreTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavigationScreen(
                        navController = navController,
                        modifier = Modifier,
                        isFirstLaunch = isFirstLaunch
                    )
                }
          }
        }
    }
}
