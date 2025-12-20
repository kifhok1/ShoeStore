package com.example.shoestore.data.model

import android.R
import com.google.gson.annotations.SerializedName

data class ProductDto(
    val id: String,
    val title: String,
    val cost: Int,
    val category: String,
    @SerializedName("is_best_seller") val isBestSeller: Boolean,
    @SerializedName("category_id") val categoryId: String,
    val description: String
)
