// ui/theme/viewModel/SignUpViewModel.kt
package com.example.shoestore.ui.theme.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoestore.data.AuthStore
import com.example.shoestore.data.RetrofitInstance
import com.example.shoestore.data.model.SignInResponse
import com.example.shoestore.data.model.SignUpRequest
import com.example.shoestore.data.model.SignUpResponse
import com.example.shoestore.data.model.SignUpState
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(application: Application) : AndroidViewModel(application) {

    private val authStore = AuthStore(application)

    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun signUp(signUpRequest: SignUpRequest) {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            try {
                val response = RetrofitInstance.userManagementService.signUp(signUpRequest)

                if (response.isSuccessful) {
                    val body = response.body()
                    val gson = Gson()

                    // Попытка найти сессию для авто-логина
                    try {
                        val json = gson.toJson(body)
                        val session = gson.fromJson(json, SignInResponse::class.java)

                        // === СОХРАНЕНИЕ В SHAREDPREFERENCES ===
                        if (!session.access_token.isNullOrBlank() && !session.user.id.isNullOrBlank()) {
                            Log.d("SignUpViewModel", "Auto-login: Saving to SharedPreferences")
                            authStore.saveToken(session.access_token, session.user.id)
                        }
                    } catch (e: Exception) {
                        // Токена нет
                    }

                    // Попытка получить ID пользователя
                    var signUpResponse: SignUpResponse? = null
                    try {
                        val json = gson.toJson(body)
                        signUpResponse = gson.fromJson(json, SignUpResponse::class.java)
                    } catch (e: Exception) { }

                    if (signUpResponse != null) {
                        Log.v("signUp", "User created: ${signUpResponse.id}")
                    }

                    _signUpState.value = SignUpState.Success

                } else {
                    val errorMessage = "Registration failed: ${response.message()}"
                    _signUpState.value = SignUpState.Error(errorMessage)
                }
            } catch (e: Exception) {
                _signUpState.value = SignUpState.Error("Network error: ${e.message}")
            }
        }
    }

    fun resetState() {
        _signUpState.value = SignUpState.Idle
    }
}
