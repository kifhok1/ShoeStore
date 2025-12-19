package com.example.shoestore.ui.theme.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoestore.data.RetrofitInstance
import com.example.shoestore.data.model.SignUpRequest
import com.example.shoestore.data.model.SignUpResponse // Убедитесь, что импортирован этот класс
import com.example.shoestore.data.model.SignUpState
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SignUpViewModel: ViewModel() {
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun signUp(signUpRequest: SignUpRequest) {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading
            try {
                val response = RetrofitInstance.userManagementService.signUp(signUpRequest)

                if (response.isSuccessful) {
                    val body = response.body()

                    // ИСПРАВЛЕНИЕ: Преобразуем Any в SignUpResponse
                    val signUpResponse: SignUpResponse? = try {
                        val gson = Gson()
                        val json = gson.toJson(body)
                        gson.fromJson(json, SignUpResponse::class.java)
                    } catch (e: Exception) {
                        null
                    }

                    if (signUpResponse != null) {
                        // Теперь поле id доступно, так как компилятор знает тип
                        Log.v("signUp", "User id: ${signUpResponse.id}")
                        _signUpState.value = SignUpState.Success
                    } else {
                        // Если распарсить не удалось, но сервер вернул 200 OK, все равно считаем успехом
                        Log.v("signUp", "Registration successful (response parsing skipped)")
                        _signUpState.value = SignUpState.Success
                    }

                } else {
                    val errorMessage = when (response.code()) {
                        400 -> "Invalid email or password"
                        422 -> "Unable to validate email address: invalid format"
                        429 -> "Too many requests"
                        else -> "Registration failed: ${response.message()}"
                    }
                    _signUpState.value = SignUpState.Error(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is java.net.ConnectException -> "No internet connection"
                    is java.net.SocketTimeoutException -> "Connection timeout"
                    else -> "Network error: ${e.message}"
                }
                _signUpState.value = SignUpState.Error(errorMessage)
                Log.e("SignUpViewModel", e.message.toString())
            }
        }
    }

    fun resetState() {
        _signUpState.value = SignUpState.Idle
    }
}
