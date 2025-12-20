package com.example.shoestore.ui.theme.view

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.shoestore.R
import com.example.shoestore.data.model.ProductDto
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.IconButtonBack
import com.example.shoestore.ui.theme.viewModel.CatalogViewModel
import kotlinx.coroutines.launch

@Composable
fun ProductDetailsScreen(
    startProductId: String, // ID товара приходит как String из навигации
    viewModel: CatalogViewModel,
    onBackClick: () -> Unit = {}
) {
    val products by viewModel.products.collectAsState()
    val favouriteIds by viewModel.favouriteIds.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Загружаем данные, если список пуст
    LaunchedEffect(Unit) {
        // Если у вас нет метода loadAllProductsIfNeeded, используйте просто loadProducts() или getAll()
        // viewModel.loadAllProductsIfNeeded()
    }

    // Состояния загрузки и ошибки
    when {
        isLoading && products.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize().background(CustomTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "Загрузка...", color = CustomTheme.colors.text)
            }
            return
        }
        error != null && products.isEmpty() -> {
            Box(
                modifier = Modifier.fillMaxSize().background(CustomTheme.colors.background),
                contentAlignment = Alignment.Center
            ) {
                Text(text = error ?: "Ошибка", color = CustomTheme.colors.red)
            }
            return
        }
        products.isEmpty() -> {
            // Если продуктов нет вообще, не рендерим контент
            return
        }
    }

    // Вычисляем начальную страницу для Pager
    val initialIndex = remember(products, startProductId) {
        // Сравниваем ID как строки, чтобы избежать ошибок типов
        val idx = products.indexOfFirst { it.id.toString() == startProductId }
        if (idx >= 0) idx else 0
    }

    val pagerState = rememberPagerState(initialPage = initialIndex) { products.size }
    val scope = rememberCoroutineScope()

    // Текущий отображаемый продукт
    val currentDto = products[pagerState.currentPage]
    val isFavorite = favouriteIds.contains(currentDto.id)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(CustomTheme.colors.background)
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, end = 20.dp, top = 60.dp)
    ) {
        Spacer(Modifier.height(12.dp))

        // ===== TOP BAR =====
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(contentAlignment = Alignment.Center) {
                // ИСПРАВЛЕНО: передача функции, а не лямбды внутри лямбды
                IconButtonBack(onClick = onBackClick)
            }

            Text(
                text = "Sneaker Shop",
                modifier = Modifier.weight(1f),
                color = CustomTheme.colors.text,
                style = CustomTheme.typography.BodySemiBold18,
                textAlign = TextAlign.Center
            )

            IconButton(
                onClick = { /* Логика корзины */ },
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(CustomTheme.colors.block)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.cart),
                    contentDescription = "cart",
                    tint = CustomTheme.colors.text // Добавлен цвет иконки
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ===== INFO =====
        // Заголовок
        Text(
            text = currentDto.title,
            color = CustomTheme.colors.text,
            style = CustomTheme.typography.HeadingRegular26
        )

        // Категория (можно брать из category, если есть в DTO)
        Text(
            text = currentDto.category ?: "Men's Shoes",
            color = CustomTheme.colors.subTextDark,
            style = CustomTheme.typography.BodyRegular14,
            modifier = Modifier.padding(top = 8.dp)
        )

        // Цена
        // ИСПРАВЛЕНО: fontSize заменен на style, добавлено форматирование
        Text(
            text = "₽${currentDto.cost}",
            color = CustomTheme.colors.text,
            style = CustomTheme.typography.BodySemiBold18,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(Modifier.height(18.dp))

        // ===== PODIUM + PAGER =====
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            Image(
                painter = painterResource(id = R.drawable.podium),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(230.dp)
                    .padding(bottom = 20.dp),
                contentPadding = PaddingValues(horizontal = 32.dp), // Чуть больше отступ по бокам
                verticalAlignment = Alignment.Bottom
            ) { page ->
                // В реальном приложении здесь должен быть Coil/Glide: AsyncImage(model = products[page].imageUrl ...)
                Image(
                    painter = painterResource(id = R.drawable.nike),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1.1f),
                    contentScale = ContentScale.Fit
                )
            }
        }

        Spacer(Modifier.height(24.dp))

        // ===== THUMBNAILS =====
        ThumbnailsRow(
            products = products,
            selectedIndex = pagerState.currentPage,
            onSelect = { index ->
                scope.launch { pagerState.animateScrollToPage(index) }
            }
        )

        Spacer(Modifier.height(40.dp))

        // ===== DESCRIPTION =====
        ExpandableText(
            text = currentDto.description ?: "",
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(24.dp))

        // ===== FAVORITE BUTTON =====
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(CustomTheme.colors.block)
                    .clickable { viewModel.toggleFavourite(currentDto.id) },
                contentAlignment = Alignment.Center
            ) {
                // ИСПРАВЛЕНО: imageVector заменен на painter
                Icon(
                    painter = if (isFavorite) {
                        painterResource(R.drawable.favorite_fill)
                    } else {
                        painterResource(R.drawable.favorite)
                    },
                    contentDescription = "Fav",
                    tint = if (isFavorite) CustomTheme.colors.red else CustomTheme.colors.text,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun ThumbnailsRow(
    products: List<ProductDto>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit
) {
    val listState = rememberLazyListState()

    // Автопрокрутка ленты миниатюр к выбранному элементу
    LaunchedEffect(selectedIndex) {
        listState.animateScrollToItem(selectedIndex)
    }

    LazyRow(
        state = listState,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        itemsIndexed(products) { index, _ ->
            val isSelected = index == selectedIndex

            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CustomTheme.colors.block)
                    .border(
                        width = if (isSelected) 2.dp else 0.dp,
                        color = if (isSelected) CustomTheme.colors.accent else Transparent,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onSelect(index) },
                contentAlignment = Alignment.Center
            ) {
                // Здесь тоже лучше использовать AsyncImage
                Image(
                    painter = painterResource(id = R.drawable.nike),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
private fun ExpandableText(
    text: String,
    modifier: Modifier = Modifier
) {
    key(text) {
        var isExpanded by remember { mutableStateOf(false) }
        var isOverflowing by remember { mutableStateOf(false) }

        Column(modifier = modifier.animateContentSize()) {
            Text(
                text = text,
                color = CustomTheme.colors.subTextDark,
                style = CustomTheme.typography.BodyRegular14,
                maxLines = if (isExpanded) Int.MAX_VALUE else 3,
                overflow = TextOverflow.Ellipsis,
                onTextLayout = { tlr ->
                    // Проверяем, есть ли скрытый текст, только если мы не раскрыты
                    if (!isExpanded) {
                        isOverflowing = tlr.hasVisualOverflow
                    }
                }
            )

            if (isOverflowing || isExpanded) {
                Text(
                    text = if (isExpanded) "Скрыть" else "Подробнее",
                    color = CustomTheme.colors.accent,
                    style = CustomTheme.typography.BodyRegular14,
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { isExpanded = !isExpanded }
                        .align(Alignment.End)
                )
            }
        }
    }
}
