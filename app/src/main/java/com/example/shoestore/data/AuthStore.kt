package com.example.shoestore.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
private val Context.ds by preferencesDataStore("auth_store")


class AuthStore(private val context: Context) {
    private val ACCESS_TOKEN = stringPreferencesKey("access_token")
    private val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
    private val USER_ID = stringPreferencesKey("user_id")

    val accessToken: Flow<String?> = context.ds.data.map { it[ACCESS_TOKEN] }
    val refreshToken: Flow<String?> = context.ds.data.map { it[REFRESH_TOKEN] }
    val userId: Flow<String?> = context.ds.data.map { it[USER_ID] }

    suspend fun saveAccessToken(token: String) {
        context.ds.edit { it[ACCESS_TOKEN] = token }
    }

    suspend fun saveRefreshToken(token: String) {
        context.ds.edit { it[REFRESH_TOKEN] = token }
    }

    suspend fun saveUserId(userId: String) {
        context.ds.edit { it[USER_ID] = userId }
    }

    suspend fun clear() {
        context.ds.edit { it.clear() }
    }
}