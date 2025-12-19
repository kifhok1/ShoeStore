package com.example.shoestore.data.service

import com.example.shoestore.data.model.FavouriteDto
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

interface FavouriteService {
    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY",
        "Content-Type: application/json"
    )
    @POST("rest/v1/favourite")
    suspend fun addFavourite(
        @Body body: FavouriteDto
    ): List<FavouriteDto>

    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY"
    )
    @DELETE("rest/v1/favourite")
    suspend fun deleteFavourite(
        // сюда уже передаём строку вида "eq.<uuid>"
        @Query("product_id") productIdEq: String,
        @Query("user_id") userIdEq: String
    ): Unit

    @Headers(
        "apikey: $API_KEY",
        "Authorization: Bearer $API_KEY"
    )
    @GET("rest/v1/favourite")
    suspend fun getFavouritesByUser(
        @Query("select") select: String = "product_id",
        @Query("user_id") userIdEq: String      // "eq.<user_id>"
    ): List<FavouriteDto>
}