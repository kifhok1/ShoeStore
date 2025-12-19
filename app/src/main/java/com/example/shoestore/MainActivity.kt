package com.example.shoestore

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.shoestore.data.nav.NavigationScreen
import com.example.shoestore.ui.theme.ShoeStoreTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Включает режим от края до края
        setContent {
            val navController = rememberNavController()
            ShoeStoreTheme {
                // Scaffold предоставляет отступы (innerPadding) для учета системных баров
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    NavigationScreen(
                        navController = navController,
                        // Важно: передаем отступы в NavigationScreen, чтобы контент не перекрывался системными панелями
                        modifier = Modifier
                    )
                }
            }
        }
    }
}
