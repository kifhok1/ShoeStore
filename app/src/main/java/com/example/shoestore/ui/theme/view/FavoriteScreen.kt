package com.example.shoestore.ui.theme.view

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoestore.R
import com.example.shoestore.data.AuthStore
import com.example.shoestore.data.model.ProductCardData
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.ProductCard
import com.example.shoestore.ui.theme.viewModel.CatalogViewModel

private const val TAG = "FavoriteScreen"

@Composable
fun FavoriteScreen(
    viewModel: CatalogViewModel,
    authStore: AuthStore,
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {},
    onProductClick: (ProductCardData) -> Unit = {}
) {
    // КЛЮЧЕВОЕ ИЗМЕНЕНИЕ: читаем favoriteProducts (не products!)
    val favoriteProductsList by viewModel.favoriteProducts.collectAsState()
    val favouriteIds by viewModel.favouriteIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    // Загружаем данные при открытии экрана
    LaunchedEffect(Unit) {
        Log.d(TAG, "Screen opened, loading favorites...")
        viewModel.loadFavoriteProducts()
    }

    // Логгируем список для отладки
    LaunchedEffect(favoriteProductsList) {
        Log.d(TAG, "UI received ${favoriteProductsList.size} favorite products")
    }

    // Преобразуем в карточки для UI
    val cardItems = favoriteProductsList.map { dto ->
        dto.toProductCardData(
            isFavorite = favouriteIds.contains(dto.id),
            onFavoriteClick = {
                viewModel.toggleFavourite(dto.id)
            }
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CustomTheme.colors.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 90.dp, start = 16.dp, end = 16.dp, top = 12.dp)
        ) {
            // Верхний бар
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(CustomTheme.colors.block)
                        .clickable { onBackClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = stringResource(R.string.Favourite),
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 32.dp),
                    textAlign = TextAlign.Center,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))

            // Индикатор загрузки
            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            // Пустой список
            else if (cardItems.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Избранное пусто",
                        fontSize = 16.sp,
                        color = CustomTheme.colors.text.copy(alpha = 0.6f)
                    )
                }
            }
            // Сетка товаров
            else {
                Log.d(TAG, "Drawing grid with ${cardItems.size} items")
                LazyVerticalGrid(
                    modifier = Modifier.fillMaxSize(),
                    columns = GridCells.Fixed(2),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp, top = 4.dp)
                ) {
                    items(cardItems) { item ->
                        ProductCard(
                            data = item,
                            modifier = Modifier.clickable { onProductClick(item) }
                        )
                    }
                }
            }
        }
    }
}
