package com.example.shoestore.ui.theme.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.example.shoestore.data.RetrofitInstance
import com.example.shoestore.data.model.SignUpRequest
import com.example.shoestore.data.model.SignUpState

class SignUpViewModel: ViewModel() {
    private val _signUpState = MutableStateFlow<SignUpState>(SignUpState.Idle)
    val signUpState: StateFlow<SignUpState> = _signUpState

    fun signUp(signUpRequest: SignUpRequest) {
        viewModelScope.launch {
            _signUpState.value = SignUpState.Loading // 1. Включаем индикацию загрузки
            try {
                val response = RetrofitInstance.userManagementService.signUp(signUpRequest)
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.v("signUp", "User id: ${it.id}")
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

    // Метод для сброса ошибки, чтобы закрыть диалог и вернуть UI в исходное состояние
    fun resetState() {
        _signUpState.value = SignUpState.Idle
    }
}