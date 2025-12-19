package com.example.shoestore.ui.theme.view

import androidx.compose.foundation.Image
import com.example.shoestore.R
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.shoestore.data.model.ProductCardData
import com.example.shoestore.ui.theme.CustomTheme
import com.example.shoestore.ui.theme.components.ProductCard
import com.example.shoestore.ui.theme.viewModel.HomeViewModel


@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onFabClick: () -> Unit = {},
    onTabSelected: (Int) -> Unit = {},
    onOpenCatalog: (String) -> Unit = {},
    viewModel: HomeViewModel = viewModel()
)  {
    var activeTab by remember { mutableIntStateOf(0) }
    var query by remember { mutableStateOf("") }



    val popularProducts = remember {
        listOf(
            ProductCardData(
                id = "1",
                imageRes = R.drawable.nike,
                label = true,
                title = "Nike Air Max",
                price = "₽752.00"
            ),
            ProductCardData(
                id = "2",
                imageRes = R.drawable.nike,
                label = true,
                title = "Nike Air Max",
                price = "₽752.00"
            )
        )
    }

    val categories by viewModel.categories.collectAsState()
    val selectedCategory by viewModel.selectedIndex.collectAsState()
    val isLoadingCategories by viewModel.isLoading.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CustomTheme.colors.background)
            .padding(top=51.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(bottom = 90.dp)
        ) {
            // Header
            Text(
                text = stringResource(R.string.Explore),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 18.dp),
                style = CustomTheme.typography.HeadingRegular32,
                color = CustomTheme.colors.text,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.weight(1f),
                    placeholder = {
                        Text(
                            stringResource(R.string.Looking_for_shoes),
                            style = CustomTheme.typography.BodyRegular12,
                            color = CustomTheme.colors.hint,
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CustomTheme.colors.block,
                        unfocusedContainerColor = CustomTheme.colors.block,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    leadingIcon = {
                        Image(
                            painter = painterResource(id = R.drawable.search),
                            contentDescription = "Search",
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                Spacer(Modifier.width(12.dp))

                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(CustomTheme.colors.accent)
                        .clickable { /* TODO filter */ },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sliders),
                        contentDescription = "Filter",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.Select_Category),
                modifier = Modifier.padding(start = 16.dp),
                style = CustomTheme.typography.BodyMedium16,
                color = CustomTheme.colors.text,
            )

            Spacer(Modifier.height(10.dp))

            if (isLoadingCategories) {
                Text(
                    text = "...",
                    modifier = Modifier.padding(horizontal = 16.dp),
                    fontSize = 12.sp,
                    color = CustomTheme.colors.subTextDark
                )
            } else {
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(categories) { index, cat ->
                        val isSelected = index == selectedCategory

                        Box(
                            modifier = Modifier
                                .height(40.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(CustomTheme.colors.block)
                                .clickable {
                                    viewModel.selectCategory(index)
                                    onOpenCatalog(cat.title)
                                }
                                .padding(horizontal = 18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat.title,
                                fontSize = 12.sp,
                                color = CustomTheme.colors.text
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.Popular_Shoes),
                    style = CustomTheme.typography.BodyMedium16,
                    color = CustomTheme.colors.text
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.See_all),
                    style = CustomTheme.typography.BodyRegular12,
                    color = CustomTheme.colors.accent,
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }

            Spacer(Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(210.dp)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                userScrollEnabled = false
            ) {
                items(popularProducts) { product ->
                    ProductCard(
                        data = product,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(Modifier.height(18.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.New_Arrivals),
                    style = CustomTheme.typography.BodyMedium16,
                    color = CustomTheme.colors.text
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = stringResource(R.string.See_all),
                    style = CustomTheme.typography.BodyRegular12,
                    color = CustomTheme.colors.accent,
                    modifier = Modifier.clickable { /* TODO */ }
                )
            }

            Spacer(Modifier.height(12.dp))

            Image(
                painter = painterResource(id = R.drawable.promo),
                contentDescription = "Promo",
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth()
                    .height(110.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }
    }
}

