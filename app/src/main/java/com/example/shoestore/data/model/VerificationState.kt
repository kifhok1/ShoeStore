package com.example.shoestore.data.model

sealed class VerificationState {
    object Idle : VerificationState()
    object Loading : VerificationState()
    data class Success(
        val type: OtpType,
        val data: Any? = null
    ) : VerificationState()
    data class Error(val message: String) : VerificationState()
}