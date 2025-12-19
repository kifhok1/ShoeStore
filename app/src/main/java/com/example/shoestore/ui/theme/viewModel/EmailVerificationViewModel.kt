package com.example.shoestore.ui.theme.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoestore.data.RetrofitInstance
import com.example.shoestore.data.model.OtpType
import com.example.shoestore.data.model.RecoveryState
import com.example.shoestore.data.model.VerificationState
import com.example.shoestore.data.model.VerifyOtpRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.ConnectException
import java.net.SocketTimeoutException

class EmailVerificationViewModel : ViewModel() {

    private val _verificationState = MutableStateFlow<VerificationState>(VerificationState.Idle)
    val verificationState: StateFlow<VerificationState> = _verificationState

    private val _recoveryState = MutableStateFlow<RecoveryState?>(null)
    val recoveryState: StateFlow<RecoveryState?> = _recoveryState

    private val _resendState = MutableStateFlow<Boolean>(false)
    val resendState: StateFlow<Boolean> = _resendState

    fun verifyOtp(email: String, otpCode: String, type: OtpType) {
        verifyOtpInternal(email, otpCode, type)
    }

    fun resendOtp(email: String, type: OtpType) {
        viewModelScope.launch {
            try {
                val requestType = if (type == OtpType.RECOVERY) "recovery" else "signup"

                // ИСПРАВЛЕНИЕ: Добавлен параметр token = "", так как VerifyOtpRequest требует его.
                // Supabase при обработке запроса resend проигнорирует поле token.
                val response = RetrofitInstance.userManagementService.resendOtp(
                    VerifyOtpRequest(email = email, token = "", type = requestType)
                )

                if (response.isSuccessful) {
                    Log.d("EmailVerificationVM", "Resend successful")
                } else {
                    Log.e("EmailVerificationVM", "Resend failed: ${response.code()}")
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

                // Map local OtpType to Supabase API type string
                val apiType = if (otpType == OtpType.RECOVERY) "recovery" else "signup"

                val response = RetrofitInstance.userManagementService.verifyOtp(
                    VerifyOtpRequest(
                        email = email,
                        token = otpCode,
                        type = apiType
                    )
                )

                if (response.isSuccessful) {
                    response.body()?.let { responseBody ->
                        _verificationState.value = VerificationState.Success(
                            type = otpType,
                            data = responseBody
                        )
                    } ?: run {
                        // Even if body is empty, 200 OK means success usually
                        _verificationState.value = VerificationState.Success(type = otpType, data = Any())
                    }
                } else {
                    val errorMessage = parseVerificationError(response.code(), response.message(), otpType)
                    _verificationState.value = VerificationState.Error(errorMessage)
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is ConnectException -> "No internet connection"
                    is SocketTimeoutException -> "Connection timeout"
                    else -> "Verification failed: ${e.message}"
                }
                _verificationState.value = VerificationState.Error(errorMessage)
            }
        }
    }

    private fun parseVerificationError(code: Int, message: String, otpType: OtpType): String {
        return when (code) {
            400 -> "Invalid OTP code"
            401 -> "OTP expired or invalid"
            404 -> "Email not found"
            429 -> "Too many attempts. Please try again later."
            else -> "Verification failed: $message"
        }
    }

    fun resetState() {
        _verificationState.value = VerificationState.Idle
        _recoveryState.value = null
    }
}
