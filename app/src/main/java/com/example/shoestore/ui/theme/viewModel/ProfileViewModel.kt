// ui/theme/viewModel/ProfileViewModel.kt
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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

private const val TAG = "ProfileVM"

class ProfileViewModel(app: Application) : AndroidViewModel(app) {

    private val authStore = AuthStore(app.applicationContext)

    private val _state = MutableStateFlow<ProfileState>(ProfileState.Loading)
    val state: StateFlow<ProfileState> = _state

    init {
        loadProfile()
    }

    fun loadProfile() {
        viewModelScope.launch {
            _state.value = ProfileState.Loading

            val token = authStore.getToken()
            val userId = authStore.getUserId()

            if (token.isNullOrBlank() || userId.isNullOrBlank()) {
                Log.e(TAG, "loadProfile: userId or token is null")
                _state.value = ProfileState.Error("Ошибка авторизации")
                return@launch
            }

            Log.d(TAG, "loadProfile: userId=$userId")

            try {
                // Синтаксис Supabase: eq.<id>
                val filter = "eq.$userId"
                val bearer = "Bearer $token"

                val resp = RetrofitInstance.profileService.getProfileByUserId(
                    apiKey = API_KEY,
                    bearer = bearer,
                    userIdEq = filter
                )

                Log.d(TAG, "loadProfile filter=$filter: code=${resp.code()}")

                if (resp.isSuccessful) {
                    val list = resp.body().orEmpty()
                    Log.d(TAG, "loadProfile: listSize=${list.size}")

                    val profile = list.firstOrNull()
                    if (profile != null) {
                        _state.value = ProfileState.Ready(profile)
                    } else {
                        // Если профиля нет, можно создать пустой или показать пустую форму
                        Log.w(TAG, "No profile found (empty list)")
                        // В этой версии просто переключаем на Ready, но можно вызвать createEmptyProfile
                        _state.value = ProfileState.Error("Профиль не найден. Попробуйте сохранить данные.")
                    }
                } else {
                    Log.w(TAG, "No profile found, code=${resp.code()}")
                    _state.value = ProfileState.Error("Ошибка загрузки: ${resp.code()}")
                }
            } catch (e: Exception) {
                Log.e(TAG, "loadProfile exception=${e.message}", e)
                _state.value = ProfileState.Error("Ошибка сети: ${e.message}")
            }
        }
    }

    fun saveProfile(firstname: String, lastname: String, address: String, phone: String) {
        val userId = authStore.getUserId()
        val token = authStore.getToken()

        if (userId == null || token == null) {
            Log.e(TAG, "saveProfile: userId is null")
            return
        }

        Log.d(TAG, "saveProfile: userId=$userId")

        viewModelScope.launch {
            // Оптимистичное UI обновление или Loading
            val currentProfile = (_state.value as? ProfileState.Ready)?.profile
            if (currentProfile != null) {
                _state.value = ProfileState.Saving(currentProfile)
            } else {
                _state.value = ProfileState.Loading
            }

            try {
                // Подготовка данных. Используем DTO вместо mapOf
                val updateBody = ProfileUpdateDto(
                    firstname = firstname.ifBlank { null },
                    lastname = lastname.ifBlank { null },
                    address = address.ifBlank { null },
                    phone = phone.ifBlank { null }
                )

                val filter = "eq.$userId"
                val bearer = "Bearer $token"

                // 1. UPDATE (Попытка обновления)
                Log.d(TAG, "Trying UPDATE: filter=$filter, body=$updateBody")

                val updateResp = RetrofitInstance.profileService.updateProfileByUserId(
                    apiKey = API_KEY,
                    bearer = bearer,
                    userIdEq = filter,
                    body = updateBody
                )

                Log.d(TAG, "UPDATE: code=${updateResp.code()}")

                if (updateResp.isSuccessful) {
                    val updated = updateResp.body()?.firstOrNull()
                    if (updated != null) {
                        Log.d(TAG, "Profile UPDATED successfully")
                        _state.value = ProfileState.Ready(updated)
                        return@launch
                    }
                }

                // 2. CREATE (Если UPDATE не прошел или вернул пустоту — создаем)
                Log.w(TAG, "UPDATE failed or returned empty, trying CREATE")

                val upsertBody = ProfileUpsertDto(
                    user_id = userId,
                    firstname = firstname.ifBlank { null },
                    lastname = lastname.ifBlank { null },
                    address = address.ifBlank { null },
                    phone = phone.ifBlank { null }
                )

                val createResp = RetrofitInstance.profileService.createProfile(
                    apiKey = API_KEY,
                    bearer = bearer,
                    body = upsertBody
                )

                Log.d(TAG, "CREATE: code=${createResp.code()}")

                if (createResp.isSuccessful) {
                    val created = createResp.body()?.firstOrNull()
                    if (created != null) {
                        Log.d(TAG, "Profile CREATED successfully")
                        _state.value = ProfileState.Ready(created)
                    } else {
                        _state.value = ProfileState.Error("Профиль создан, но вернулся пустой ответ")
                    }
                } else {
                    val err = createResp.errorBody()?.string()
                    Log.e(TAG, "Both failed: UPDATE=${updateResp.code()}, CREATE=${createResp.code()}. Error: $err")
                    _state.value = ProfileState.Error("Не удалось сохранить профиль")
                }
            } catch (e: Exception) {
                Log.e(TAG, "saveProfile exception=${e.message}", e)
                _state.value = ProfileState.Error("Ошибка: ${e.message}")
            }
        }
    }

    fun saveAvatar(photoUri: Uri) {
        viewModelScope.launch {
            Log.d("Avatar", "saveAvatar() started uri=$photoUri")

            // Используем .first() для DataStore (судя по вашему коду у вас Flow)
            val userId = authStore.getUserId()
            val token = authStore.getToken()

            if (token.isNullOrBlank() || userId.isNullOrBlank()) {
                _state.value = ProfileState.Error("Нет токена или userId.")
                return@launch
            }

            // === 1. СЖАТИЕ ФОТО (КРИТИЧЕСКИ ВАЖНО) ===
            // Без этого 5МБ фото отвалится по таймауту на медленном интернете
            val bytes = withContext(Dispatchers.IO) {
                try {
                    val resolver = getApplication<Application>().contentResolver
                    resolver.openInputStream(photoUri)?.use { inputStream ->
                        // Читаем битмап
                        val original = android.graphics.BitmapFactory.decodeStream(inputStream)
                        // Сжимаем (JPEG, качество 60)
                        val outStream = java.io.ByteArrayOutputStream()
                        original.compress(android.graphics.Bitmap.CompressFormat.JPEG, 60, outStream)
                        outStream.toByteArray()
                    }
                } catch (e: Exception) {
                    Log.e("Avatar", "Compression failed", e)
                    null
                }
            }

            if (bytes == null) {
                _state.value = ProfileState.Error("Не удалось прочитать/сжать фото")
                return@launch
            }

            Log.d("Avatar", "Image compressed. Size: ${bytes.size / 1024} KB")

            // === 2. ЗАГРУЗКА В STORAGE ===
            val bucket = "avatars"
            val path = "$userId/${System.currentTimeMillis()}.jpg"
            val contentType = "image/jpeg"
            val body = bytes.toRequestBody(contentType.toMediaType())

            Log.d("Avatar", "Uploading to $bucket/$path...")

            try {
                val uploadResp = RetrofitInstance.storageService.upload(
                    apiKey = API_KEY,
                    bearer = "Bearer $token",
                    contentType = contentType,
                    bucket = bucket,
                    path = path,
                    body = body
                )

                if (!uploadResp.isSuccessful) {
                    val err = uploadResp.errorBody()?.string()
                    Log.e("Avatar", "Upload failed: $err")
                    _state.value = ProfileState.Error("Ошибка загрузки: ${uploadResp.code()}")
                    return@launch
                }

                // === 3. ОБНОВЛЕНИЕ ССЫЛКИ В БД ===
                val publicUrl = "${RetrofitInstance.SUPABASE_URL}storage/v1/object/public/$bucket/$path"
                Log.d("Avatar", "Upload success. Public URL: $publicUrl")

                val updateResp = RetrofitInstance.profileService.updateProfileByUserId(
                    apiKey = API_KEY,
                    bearer = "Bearer $token",
                    userIdEq = "eq.$userId",
                    body = ProfileUpdateDto(photo = publicUrl)
                )

                if (updateResp.isSuccessful) {
                    val updated = updateResp.body()?.firstOrNull()
                    if (updated != null) {
                        Log.d("Avatar", "Profile updated successfully")
                        _state.value = ProfileState.Ready(updated)
                    } else {
                        Log.w("Avatar", "Update success but empty body. Reloading...")
                        loadProfile()
                    }
                } else {
                    val err = updateResp.errorBody()?.string()
                    Log.e("Avatar", "DB Update failed: $err")
                    _state.value = ProfileState.Error("Фото загружено, но профиль не обновился")
                }
            } catch (e: Exception) {
                Log.e("Avatar", "Exception", e)
                _state.value = ProfileState.Error("Ошибка: ${e.message}")
            }
        }
    }
}
