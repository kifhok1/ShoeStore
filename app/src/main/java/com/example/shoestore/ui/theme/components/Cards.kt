package com.example.shoestore.ui.theme.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.shoestore.R
import com.example.shoestore.data.model.ProductCardData
import com.example.shoestore.ui.theme.CustomTheme


@Composable
fun ProductCard(
    data: ProductCardData,
    modifier: Modifier = Modifier
) {
    var isFavorite by remember(data.isFavorite) { mutableStateOf(data.isFavorite) }
    var isInCart by remember(data.isInCart) { mutableStateOf(data.isInCart) }

    Box(
        modifier = modifier.height(180.dp).width(160.dp).shadow(
            elevation = 4.dp,
            shape = RoundedCornerShape(12.dp),
            clip = false
        ).background(
            color = CustomTheme.colors.block,
            shape = RoundedCornerShape(12.dp)
        )
    ) {
        Column(
            modifier = Modifier.size(180.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.padding(start = 10.dp, end = 10.dp).fillMaxWidth().weight(1f)
            ) {
                Box(
                    modifier = Modifier.fillMaxWidth().weight(1f).clip(RoundedCornerShape(8.dp))
                ) {
                    Image(
                        painter = painterResource(id = data.imageRes),
                        contentDescription = data.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )

                    Box(
                        modifier = Modifier.align(Alignment.TopStart).padding(top = 10.dp).size(28.dp).clip(CircleShape)
                            .background(
                                color = CustomTheme.colors.background,
                                shape = CircleShape
                            ).clickable {
                                isFavorite = !isFavorite
                                data.onFavoriteClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isFavorite) {
                                    R.drawable.favorite_fill
                                } else {
                                    R.drawable.favorite
                                }
                            ),
                            contentDescription = "Favorite",
                            modifier = Modifier.size(14.dp),
                            tint = if (isFavorite) {
                                CustomTheme.colors.red
                            } else {
                                CustomTheme.colors.text
                            }
                        )
                    }
                }
                if (data.label){
                    Text(
                        text = "BEST SELLER",
                        style = CustomTheme.typography.BodyRegular12,
                        color = CustomTheme.colors.accent,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                } else {
                    Spacer(modifier = Modifier.height(12.dp))
                }


                Text(
                    text = data.title,
                    style = CustomTheme.typography.BodyRegular16,
                    color = CustomTheme.colors.hint,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }


            Row(
                modifier = Modifier.height(30.dp).padding(start = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = data.price,
                    style = CustomTheme.typography.BodyRegular12,
                    color = CustomTheme.colors.text,
                    modifier = Modifier.padding(top = 1.dp),
                    textAlign = TextAlign.Start
                )

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier.fillMaxHeight().size(30.dp).background(
                        color = CustomTheme.colors.accent,
                        shape = RoundedCornerShape(
                            topStart = 12.dp,
                            bottomStart = 0.dp,
                            bottomEnd = 12.dp,
                            topEnd = 0.dp
                        )
                    ).clickable {
                        isInCart = !isInCart
                        data.onAddClick()
                    },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(
                            id = if (isInCart) {
                                R.drawable.cart
                            } else {
                                R.drawable.add
                            }
                        ),
                        contentDescription = if (isInCart) {
                            "In Cart"
                        } else {
                            "Add"
                        },
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }
        }
    }
}