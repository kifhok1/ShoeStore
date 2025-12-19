// data/AuthStore.kt
package com.example.shoestore.data

import android.content.Context
import android.content.SharedPreferences

class AuthStore(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_USER_ID = "user_id"
    }

    // Сохранение данных
    fun saveToken(token: String, userId: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, token)
            .putString(KEY_USER_ID, userId)
            .apply() // apply сохраняет асинхронно, не блокируя UI
    }

    // Получение токена (синхронно)
    fun getToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    // Получение ID (синхронно)
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    // Очистка данных (выход)
    fun clear() {
        prefs.edit().clear().apply()
    }
}
