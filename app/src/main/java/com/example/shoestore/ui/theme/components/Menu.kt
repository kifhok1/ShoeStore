package com.example.shoestore.ui.theme.components

import android.graphics.Matrix
import android.graphics.RectF
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.*
import androidx.core.graphics.PathParser
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.shoestore.R
import com.example.shoestore.ui.theme.CustomTheme

private const val MENU_SVG_PATH = "M121.7,20.5C66.8,21.7,17.7,7.3,0,0v106h375V0c-27,19.5-98.1,20-119.7,20c-21.5,0-25.5,3-25.5,11.5s4.6,31.7-22,35c-52.1,6.5-62.2-11-63.1-22C143.7,31.5,146.2,20.5,121.7,20.5z"

object TabRoutes {
    const val HOME = "tab_home"
    const val FAVORITE = "tab_favorite"
    const val CART = "tab_cart"
    const val ORDERS = "tab_orders"
    const val PROFILE = "tab_profile"
}

@Composable
fun ShoeBottomNavigationBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val barHeight = 100.dp
    val bgHeight = 86.dp

    Box(modifier = modifier.fillMaxWidth().height(barHeight)) {
        // 1. Белая подложка (Shape)
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bgHeight)
                .shadow(16.dp, SvgPathShape(MENU_SVG_PATH), spotColor = Color.Black),
            color = CustomTheme.colors.block,
            shape = SvgPathShape(MENU_SVG_PATH)
        ) {}

        // 2. Центральная кнопка (FAB)
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
            FloatingButton(onClick = { navigateToTab(navController, TabRoutes.CART) })
        }

        // 3. Иконки
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(bgHeight)
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            MenuIcon(R.drawable.home, currentRoute == TabRoutes.HOME) { navigateToTab(navController, TabRoutes.HOME) }
            MenuIcon(R.drawable.favorite, currentRoute == TabRoutes.FAVORITE) { navigateToTab(navController, TabRoutes.FAVORITE) }

            Spacer(modifier = Modifier.width(60.dp)) // Место под FAB

            MenuIcon(R.drawable.orders, currentRoute == TabRoutes.ORDERS) { navigateToTab(navController, TabRoutes.ORDERS) }
            MenuIcon(R.drawable.profile, currentRoute == TabRoutes.PROFILE) { navigateToTab(navController, TabRoutes.PROFILE) }
        }
    }
}

// Хелпер для навигации по табам
private fun navigateToTab(navController: NavController, route: String) {
    navController.navigate(route) {
        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun FloatingButton(onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .size(56.dp)
            .offset(y = 12.dp)
            .shadow(8.dp, CircleShape)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        color = CustomTheme.colors.accent
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(painter = painterResource(id = R.drawable.bag_2), contentDescription = "Cart", tint = Color.White, modifier = Modifier.size(24.dp))
        }
    }
}

@Composable
private fun MenuIcon(iconRes: Int, isSelected: Boolean, onClick: () -> Unit) {
    val tint = if (isSelected) CustomTheme.colors.accent else CustomTheme.colors.subTextDark
    Box(
        modifier = Modifier.size(48.dp).clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(painter = painterResource(id = iconRes), contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
    }
}

// Парсер SVG в Shape
class SvgPathShape(private val pathData: String) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val path = try { PathParser.createPathFromPathData(pathData) } catch (e: Exception) { android.graphics.Path() }
        val bounds = RectF(); path.computeBounds(bounds, true)
        val matrix = Matrix()
        var scaleX = 1f; var scaleY = 1f
        if (bounds.width() > 0 && bounds.height() > 0) {
            scaleX = size.width / bounds.width(); scaleY = size.height / bounds.height()
        }
        matrix.setScale(scaleX, scaleY)
        path.transform(matrix)
        return Outline.Generic(path.asComposePath())
    }
}