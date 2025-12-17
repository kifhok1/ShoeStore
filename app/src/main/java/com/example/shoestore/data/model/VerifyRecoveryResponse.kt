package com.example.shoestore.data.model

data class VerifyRecoveryResponse(
    val success: Boolean,
    val message: String,
    val reset_token: String? = null
)
