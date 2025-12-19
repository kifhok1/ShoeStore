// ui/theme/viewModel/SignInViewModel.kt
package com.example.shoestore.ui.theme.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoestore.data.AuthStore
import com.example.shoestore.data.RetrofitInstance
import com.example.shoestore.data.model.ChangePasswordRequest
import com.example.shoestore.data.model.ChangePasswordState
import com.example.shoestore.data.model.SignInRequest
import com.example.shoestore.data.model.SignInResponse
import com.example.shoestore.data.model.SignInState
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInViewModel(application: Application) : AndroidViewModel(application) {

    // Инициализируем AuthStore с контекстом приложения
    private val authStore = AuthStore(application)

    private val _signInState = MutableStateFlow<SignInState>(SignInState.Idle)
    val signInState: StateFlow<SignInState> = _signInState

    private val _changePasswordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val changePasswordState: StateFlow<ChangePasswordState> = _changePasswordState.asStateFlow()

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            _signInState.value = SignInState.Loading
            try {
                val response = RetrofitInstance.userManagementService.signIn(
                    SignInRequest(email, password)
                )

                if (response.isSuccessful) {
                    val body = response.body()

                    val signInResponse: SignInResponse? = if (body is SignInResponse) {
                        body
                    } else {
                        try {
                            val gson = Gson()
                            val json = gson.toJson(body)
                            gson.fromJson(json, SignInResponse::class.java)
                        } catch (e: Exception) {
                            null
                        }
                    }

                    if (signInResponse != null) {
                        // === СОХРАНЕНИЕ В SHAREDPREFERENCES ===
                        Log.d("SignInViewModel", "Saving token to SharedPreferences")
                        authStore.saveToken(signInResponse.access_token, signInResponse.user.id)

                        _signInState.value = SignInState.Success
                    } else {
                        _signInState.value = SignInState.Error("Failed to parse response")
                    }
                } else {
                    val errorMessage = parseSignInError(response.code(), response.message())
                    _signInState.value = SignInState.Error(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is java.net.ConnectException -> "No internet connection"
                    else -> "Authentication failed: ${e.message}"
                }
                _signInState.value = SignInState.Error(errorMessage)
            }
        }
    }

    fun changePassword(token: String, newPassword: String) {
        viewModelScope.launch {
            _changePasswordState.value = ChangePasswordState.Loading
            try {
                val response = RetrofitInstance.userManagementService.changePassword(
                    token = "Bearer $token",
                    changePasswordRequest = ChangePasswordRequest(password = newPassword)
                )

                if (response.isSuccessful && response.body() != null) {
                    _changePasswordState.value = ChangePasswordState.Success
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Unknown error"
                    _changePasswordState.value = ChangePasswordState.Error("Failed: $errorMessage")
                }
            } catch (e: Exception) {
                _changePasswordState.value = ChangePasswordState.Error("Network error: ${e.message}")
            }
        }
    }

    fun resetChangePasswordState() {
        _changePasswordState.value = ChangePasswordState.Idle
    }

    private fun parseSignInError(code: Int, message: String): String {
        return when (code) {
            400 -> "Invalid email or password"
            401 -> "Invalid login credentials"
            else -> "Login failed: $message"
        }
    }

    fun resetState() {
        _signInState.value = SignInState.Idle
    }
}
