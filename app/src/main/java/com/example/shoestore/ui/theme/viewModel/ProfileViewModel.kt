package com.example.shoestore.ui.theme.viewModel;

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoestore.data.AuthStore

import com.example.shoestore.data.RetrofitInstance
import com.example.shoestore.data.model.ProfileState
import com.example.shoestore.data.model.ProfileUpdateDto
import com.example.shoestore.data.model.ProfileUpsertDto
import com.example.shoestore.data.service.API_KEY
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody


public class ProfileViewModel(app:Application) : AndroidViewModel(app){
    private val authStore = AuthStore(app.applicationContext)

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
            val state: StateFlow<ProfileState> = _state

    init {
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading

            val token = authStore.accessToken.first()
            val userId = authStore.userId.first()

            Log.d("Profile", "load(): tokenPresent=${!token.isNullOrBlank()} userId=$userId")

            if (token.isNullOrBlank() || userId.isNullOrBlank()) {
                _state.value = ProfileState.Error("Нет токена или userId. Войдите снова.")
                return@launch
            }

            val bearer = "Bearer $token"

            val getResp = RetrofitInstance.profileService.getProfileByUserId(
                    apiKey = API_KEY,
                    bearer = bearer,
                    userIdEq = "eq.$userId"
            )

            Log.d("Profile", "getProfile code=${getResp.code()} err=${getResp.errorBody()?.string()}")
            Log.d("Profile", "getProfile body=${getResp.body()}")

            if (getResp.isSuccessful) {
                val existing = getResp.body()?.firstOrNull()
                if (existing != null) {
                    Log.d("Profile", "existing profile found, photo=${existing.photo}")
                    _state.value = ProfileState.Ready(existing)
                    return@launch
                }

                // Если профиля нет — создаём строку
                val createResp = RetrofitInstance.profileService.createProfile(
                        apiKey = API_KEY,
                        bearer = bearer,
                        body = ProfileUpsertDto(user_id = userId)
                )

                Log.d("Profile", "createProfile code=${createResp.code()} err=${createResp.errorBody()?.string()}")
                Log.d("Profile", "createProfile body=${createResp.body()}")

                if (createResp.isSuccessful) {
                    val created = createResp.body()?.firstOrNull()
                    if (created != null) {
                        Log.d("Profile", "profile created, photo=${created.photo}")
                        _state.value = ProfileState.Ready(created)
                    } else {
                        _state.value = ProfileState.Error("Профиль не создан: пустой ответ")
                    }
                } else {
                    _state.value = ProfileState.Error("Ошибка создания профиля: ${createResp.code()}")
                }
            } else {
                _state.value = ProfileState.Error("Ошибка загрузки профиля: ${getResp.code()}")
            }
        }
    }

    fun save(first: String, last: String, addr: String, phone: String) {
        viewModelScope.launch {
            val current = (_state.value as? ProfileState.Ready)?.profile
            if (current == null) return@launch

                    val token = authStore.accessToken.first()
            if (token.isNullOrBlank()) {
                _state.value = ProfileState.Error("Нет токена. Войдите снова.")
                return@launch
            }

            val resp = RetrofitInstance.profileService.updateProfileByUserId(
                    apiKey = API_KEY,
                    bearer = "Bearer $token",
                    userIdEq = "eq.${current.user_id}",
                    body = ProfileUpdateDto(
                        firstname = first,
                        lastname = last,
                        address = addr,
                        phone = phone
                    )
            )

            Log.d("Profile", "save() code=${resp.code()} err=${resp.errorBody()?.string()} body=${resp.body()}")

            if (resp.isSuccessful) {
                val updated = resp.body()?.firstOrNull()
                if (updated != null) {
                    _state.value = ProfileState.Ready(updated)
                } else {
                    // на всякий случай перезагрузим
                    load()
                }
            } else {
                _state.value = ProfileState.Error("Ошибка сохранения: ${resp.code()}")
            }
        }
    }

    fun saveAvatar(photoUri: Uri) {
        viewModelScope.launch {
            Log.d("Avatar", "saveAvatar() started uri=$photoUri")

            val token = authStore.accessToken.first()
            val userId = authStore.userId.first()

            Log.d("Avatar", "tokenPresent=${!token.isNullOrBlank()} userId=$userId")

            if (token.isNullOrBlank() || userId.isNullOrBlank()) {
                _state.value = ProfileState.Error("Нет токена или userId.")
                return@launch
            }

            val bucket = "avatars"
            val path = "$userId/${System.currentTimeMillis()}.jpg"
            Log.d("Avatar", "upload target=$bucket/$path")

            val bytes = getApplication<Application>()
                    .contentResolver
                    .openInputStream(photoUri)
                    ?.use { it.readBytes() }

            Log.d("Avatar", "read bytes=${bytes?.size}")

            if (bytes == null) {
                _state.value = ProfileState.Error("Не удалось прочитать фото")
                return@launch
            }

            val contentType = "image/jpeg"
            val body = bytes.toRequestBody(contentType.toMediaType())

            val uploadResp = RetrofitInstance.storageService.upload(
                    apiKey = API_KEY,
                    bearer = "Bearer $token",
                    contentType = contentType,
                    bucket = bucket,
                    path = path,
                    body = body
            )

            Log.d("Avatar", "uploadResp code=${uploadResp.code()} err=${uploadResp.errorBody()?.string()}")

            if (!uploadResp.isSuccessful) {
                _state.value = ProfileState.Error("Ошибка загрузки фото: ${uploadResp.code()}")
                return@launch
            }

            // public URL (bucket public) [web:482]
            val publicUrl = RetrofitInstance.SUPABASE_URL +
                    "storage/v1/object/public/$bucket/$path"

            Log.d("Avatar", "publicUrl=$publicUrl")

            val updateResp = RetrofitInstance.profileService.updateProfileByUserId(
                    apiKey = API_KEY,
                    bearer = "Bearer $token",
                    userIdEq = "eq.$userId",
                    body = ProfileUpdateDto(photo = publicUrl)
            )

            Log.d("Avatar", "updateResp code=${updateResp.code()} err=${updateResp.errorBody()?.string()}")
            Log.d("Avatar", "updateResp body=${updateResp.body()}")

            if (!updateResp.isSuccessful) {
                _state.value = ProfileState.Error("Фото загружено, но профиль не обновился: ${updateResp.code()}")
                return@launch
            }

            val updated = updateResp.body()?.firstOrNull()
            if (updated != null) {
                Log.d("Avatar", "updated profile photo=${updated.photo}")
                _state.value = ProfileState.Ready(updated)
            } else {
                // если тело пустое — просто перезагрузим профиль
                Log.d("Avatar", "update body empty -> reload profile")
                load()
            }
        }
    }
}
