package com.example.shoestore.data.model

data class ProductCardData(
    val imageRes: Int,
    val label: Boolean,
    val title: String,
    val price: String,
    val isFavorite: Boolean = false,
    val isInCart: Boolean = false,
    val onFavoriteClick: () -> Unit = {},
    val onAddClick: () -> Unit = {},
    val id: String
)
