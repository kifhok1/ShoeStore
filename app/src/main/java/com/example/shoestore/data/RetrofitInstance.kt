package com.example.shoestore.data

import StorageService
import com.example.shoestore.data.service.CatalogService
import com.example.shoestore.data.service.FavouriteService
import com.example.shoestore.data.service.ProfileService
import com.example.shoestore.data.service.UserManagementService
import okhttp3.OkHttpClient
import okhttp3.Protocol
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    // ВАЖНО: URL должен заканчиваться слешем "/"
    const val SUPABASE_URL = "https://gwnpjijcdwncdbgzvbxr.supabase.co/"

    // Настраиваем клиент с увеличенными таймаутами
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(120, TimeUnit.SECONDS) // 2 минуты на соединение
        .readTimeout(120, TimeUnit.SECONDS)    // 2 минуты на чтение ответа
        .writeTimeout(120, TimeUnit.SECONDS)   // 2 минуты на отправку файла
        .protocols(listOf(Protocol.HTTP_1_1))  // Принудительно HTTP/1.1 (стабильнее для файлов)
        .build()

    // Используем 'by lazy', чтобы инициализация происходила один раз при первом обращении
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(SUPABASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client) // <-- Передаем настроенный клиент
            .build()
    }

    // Инициализируем сервисы лениво (lazy), используя уже созданный инстанс retrofit
    val userManagementService: UserManagementService by lazy {
        retrofit.create(UserManagementService::class.java)
    }

    val profileService: ProfileService by lazy {
        retrofit.create(ProfileService::class.java)
    }

    val storageService: StorageService by lazy {
        retrofit.create(StorageService::class.java)
    }

        val catalogService: CatalogService = retrofit.create(CatalogService::class.java)
        val favouriteService: FavouriteService = retrofit.create(FavouriteService::class.java)
}