package com.example.shoestore.ui.theme.view

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.shoestore.R
import com.example.shoestore.data.model.ProductCardData
import com.example.shoestore.data.model.ProductDto
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.IconButtonBack
import com.example.shoestore.ui.theme.components.ProductCard
import com.example.shoestore.ui.theme.viewModel.CatalogViewModel

@Composable
fun CatalogScreen(
    viewModel: CatalogViewModel,
    modifier: Modifier = Modifier,
    initialCategoryTitle: String = "Все",
    onBackClick: () -> Unit = {},
    onProductClick: (ProductCardData) -> Unit = {}
) {
    val categories by viewModel.categories.collectAsState()
    val selected by viewModel.selectedIndex.collectAsState()
    val productsState by viewModel.products.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val favouriteIds by viewModel.favouriteIds.collectAsState()

    LaunchedEffect(initialCategoryTitle) {
        viewModel.setInitialCategoryTitle(initialCategoryTitle)
    }

    val headerTitle = categories.getOrNull(selected)?.title ?: initialCategoryTitle

    // ИСПРАВЛЕНО: Преобразование DTO в UI-модель с помощью вспомогательной функции
    val cardItems = productsState.map { dto ->
        dto.toProductCardData(
            isFavorite = favouriteIds.contains(dto.id),
            onFavoriteClick = { viewModel.toggleFavourite(dto.id) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CustomTheme.colors.background)
            .padding(start = 16.dp, top = 62.dp, end = 16.dp)
            .statusBarsPadding()
    ) {
        // Верхняя панель
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                IconButtonBack(
                    onClick = { onBackClick() }
                )
            }

            Text(
                text = headerTitle,
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 32.dp),
                textAlign = TextAlign.Center,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = CustomTheme.colors.text
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.Select_Category),
            fontSize = 16.sp,
            style = CustomTheme.typography.BodyMedium16,
            color = CustomTheme.colors.text,
        )

        Spacer(Modifier.height(12.dp))

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(categories) { index, cat ->
                val isSelected = index == selected
                Box(
                    modifier = Modifier
                        .height(36.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isSelected) CustomTheme.colors.accent else CustomTheme.colors.block)
                        .clickable { viewModel.onCategorySelected(index) }
                        .padding(horizontal = 18.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = cat.title,
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) CustomTheme.colors.background else CustomTheme.colors.subTextDark
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                // Убедитесь, что у вас есть стандартный CircularProgressIndicator или Text
                Text(
                    text = "Загрузка...",
                    color = CustomTheme.colors.text
                )
            }
        } else {
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

// Функция-расширение для преобразования ProductDto в ProductCardData
fun ProductDto.toProductCardData(
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit
): ProductCardData {
    return ProductCardData(
        id = this.id,
        title = this.title,
        price = "₽${this.cost}", // Форматирование цены
        isFavorite = isFavorite,
        imageRes = R.drawable.nike, // Задайте это поле в соответствии с требованиями ProductCardData
        onFavoriteClick = onFavoriteClick,
        label = true
    )
}
