// ui/theme/viewModel/EmailVerificationViewModel.kt
package com.example.shoestore.ui.theme.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoestore.data.AuthStore
import com.example.shoestore.data.RetrofitInstance
import com.example.shoestore.data.model.OtpType
import com.example.shoestore.data.model.SignInResponse
import com.example.shoestore.data.model.VerificationState
import com.example.shoestore.data.model.VerifyOtpRequest
import com.google.gson.Gson
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException

class EmailVerificationViewModel(application: Application) : AndroidViewModel(application) {

    private val authStore = AuthStore(application)

    private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationState: StateFlow<VerificationState> = _verificationState

    private val _recoveryState = MutableStateFlow<Any?>(null)
    val recoveryState: StateFlow<Any?> = _recoveryState

    private val _resendState = MutableStateFlow(false)
    val resendState: StateFlow<Boolean> = _resendState

    fun verifyOtp(email: String, otpCode: String, type: OtpType) {
        verifyOtpInternal(email, otpCode, type)
    }

    fun resendOtp(email: String, type: OtpType) {
        viewModelScope.launch {
            try {
                val requestType = if (type == OtpType.RECOVERY) "recovery" else "signup"
                val response = RetrofitInstance.userManagementService.resendOtp(
                    VerifyOtpRequest(email = email, token = "", type = requestType)
                )
                if (response.isSuccessful) {
                    Log.d("EmailVerificationVM", "Resend successful")
                }
            } catch (e: Exception) {
                Log.e("EmailVerificationVM", "Resend exception: ${e.message}")
            }
        }
    }

    private fun verifyOtpInternal(email: String, otpCode: String, otpType: OtpType) {
        viewModelScope.launch {
            try {
                _verificationState.value = VerificationState.Loading
                val apiType = if (otpType == OtpType.RECOVERY) "recovery" else "signup"

                val response = RetrofitInstance.userManagementService.verifyOtp(
                    VerifyOtpRequest(email = email, token = otpCode, type = apiType)
                )

                if (response.isSuccessful) {
                    val responseBody = response.body() ?: Any()

                    try {
                        val gson = Gson()
                        val json = gson.toJson(responseBody)
                        val session = gson.fromJson(json, SignInResponse::class.java)

                        // === СОХРАНЕНИЕ В SHAREDPREFERENCES ===
                        if (!session.access_token.isNullOrBlank() && !session.user.id.isNullOrBlank()) {
                            Log.d("EmailVerificationVM", "Saving session to SharedPreferences")
                            authStore.saveToken(session.access_token, session.user.id)
                        }
                    } catch (e: Exception) {
                        // Игнорируем ошибки парсинга сессии
                    }

                    _verificationState.value = VerificationState.Success(
                        type = otpType,
                        data = responseBody
                    )
                } else {
                    val errorMessage = parseVerificationError(response.code(), response.message())
                    _verificationState.value = VerificationState.Error(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = if (e is ConnectException) "No internet connection" else e.message ?: "Error"
                _verificationState.value = VerificationState.Error(errorMessage)
            }
        }
    }

    private fun parseVerificationError(code: Int, message: String): String {
        return when (code) {
            400 -> "Invalid OTP code"
            404 -> "Email not found"
            else -> "Verification failed: $message"
        }
    }

    fun resetState() {
        _verificationState.value = VerificationState.Idle
        _recoveryState.value = null
    }
}
