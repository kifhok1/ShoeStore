package com.example.shoestore.ui.theme.viewModel

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

class ProfileViewModel(app: Application) : AndroidViewModel(app) {
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

            try {
                val getResp = RetrofitInstance.profileService.getProfileByUserId(
                    apiKey = API_KEY,
                    bearer = bearer,
                    userIdEq = "eq.$userId"
                )

                Log.d("Profile", "getProfile code=${getResp.code()}")

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

                    Log.d("Profile", "createProfile code=${createResp.code()}")

                    if (createResp.isSuccessful) {
                        val created = createResp.body()?.firstOrNull()
                        if (created != null) {
                            Log.d("Profile", "profile created, photo=${created.photo}")
                            _state.value = ProfileState.Ready(created)
                        } else {
                            _state.value = ProfileState.Error("Профиль не создан: пустой ответ")
                        }
                    } else {
                        val errorBody = createResp.errorBody()?.string()
                        Log.e("Profile", "Error creating profile: $errorBody")
                        _state.value = ProfileState.Error("Ошибка создания профиля: ${createResp.code()}")
                    }
                } else {
                    val errorBody = getResp.errorBody()?.string()
                    Log.e("Profile", "Error loading profile: $errorBody")
                    _state.value = ProfileState.Error("Ошибка загрузки профиля: ${getResp.code()}")
                }
            } catch (e: Exception) {
                Log.e("Profile", "Exception during load", e)
                _state.value = ProfileState.Error("Ошибка сети: ${e.localizedMessage}")
            }
        }
    }

    fun save(first: String, last: String, addr: String, phone: String) {
        viewModelScope.launch {
            val current = (_state.value as? ProfileState.Ready)?.profile
            if (current == null) {
                _state.value = ProfileState.Error("Профиль не загружен")
                return@launch
            }

            _state.value = ProfileState.Saving(current)

            val token = authStore.accessToken.first()
            val userId = authStore.userId.first()

            Log.d("ProfileSave", "userId=$userId")
            Log.d("ProfileSave", "tokenPresent=${!token.isNullOrBlank()}")

            if (token.isNullOrBlank() || userId.isNullOrBlank()) {
                _state.value = ProfileState.Error("Нет токена или userId. Войдите снова.")
                return@launch
            }

            try {
                val updateDto = ProfileUpdateDto(
                    firstname = first,
                    lastname = last,
                    address = addr,
                    phone = phone
                )

                Log.d("ProfileSave", "Sending update: $updateDto")
                Log.d("ProfileSave", "Filter: eq.$userId")

                val resp = RetrofitInstance.profileService.updateProfileByUserId(
                    apiKey = API_KEY,
                    bearer = "Bearer $token",
                    userIdEq = "eq.$userId",
                    body = updateDto
                )

                Log.d("ProfileSave", "Response code: ${resp.code()}")
                Log.d("ProfileSave", "Response body: ${resp.body()}")

                val errorBody = resp.errorBody()?.string()
                if (errorBody != null) {
                    Log.e("ProfileSave", "Error body: $errorBody")
                }

                if (resp.isSuccessful) {
                    val updated = resp.body()?.firstOrNull()
                    if (updated != null) {
                        Log.d("ProfileSave", "Profile updated successfully: $updated")
                        _state.value = ProfileState.Ready(updated)
                    } else {
                        Log.w("ProfileSave", "Empty response body, reloading profile")
                        load()
                    }
                } else {
                    _state.value = ProfileState.Error("Ошибка ${resp.code()}: $errorBody")
                }
            } catch (e: Exception) {
                Log.e("ProfileSave", "Exception during save", e)
                _state.value = ProfileState.Error("Ошибка сети: ${e.localizedMessage}")
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

            try {
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

                Log.d("Avatar", "uploadResp code=${uploadResp.code()}")

                if (!uploadResp.isSuccessful) {
                    val errorBody = uploadResp.errorBody()?.string()
                    Log.e("Avatar", "Upload failed: $errorBody")
                    _state.value = ProfileState.Error("Ошибка загрузки фото: ${uploadResp.code()}")
                    return@launch
                }

                // public URL (bucket public)
                val publicUrl = RetrofitInstance.SUPABASE_URL +
                        "storage/v1/object/public/$bucket/$path"

                Log.d("Avatar", "publicUrl=$publicUrl")

                val updateResp = RetrofitInstance.profileService.updateProfileByUserId(
                    apiKey = API_KEY,
                    bearer = "Bearer $token",
                    userIdEq = "eq.$userId",
                    body = ProfileUpdateDto(photo = publicUrl)
                )

                Log.d("Avatar", "updateResp code=${updateResp.code()}")

                if (!updateResp.isSuccessful) {
                    val errorBody = updateResp.errorBody()?.string()
                    Log.e("Avatar", "Profile update failed: $errorBody")
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
            } catch (e: Exception) {
                Log.e("Avatar", "Exception during avatar save", e)
                _state.value = ProfileState.Error("Ошибка загрузки аватара: ${e.localizedMessage}")
            }
        }
    }
}
