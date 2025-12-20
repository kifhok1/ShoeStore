package com.example.shoestore.data

import com.example.shoestore.data.model.CategoryDto
import com.example.shoestore.data.model.ProductDto
import com.example.shoestore.data.service.SupabaseApi

class CatalogRepository(
    private val api: SupabaseApi = RetrofitInstance.api
) {
    suspend fun getCategories(): List<CategoryDto> = api.getCategories()

    suspend fun getProducts(categoryId: String?): List<ProductDto> {
        return if (categoryId == null) {
            api.getProducts()
        } else {
            api.getProductsByCategoryId(categoryIdEq = "eq.$categoryId")
        }
    }
}