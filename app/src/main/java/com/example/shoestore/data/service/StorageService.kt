// data/service/StorageService.kt
package com.example.shoestore.data.service

import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface StorageService {
    @POST("storage/v1/object/{bucket}/{path}")
    suspend fun upload(
        @Header("Authorization") bearer: String,
        @Header("apikey") apiKey: String,
        @Header("Content-Type") contentType: String,
        @Path("bucket") bucket: String,
        @Path("path") path: String,
        @Body body: RequestBody
    ): Response<ResponseBody>
}
