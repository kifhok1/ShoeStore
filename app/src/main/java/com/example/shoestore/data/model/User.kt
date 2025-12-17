package com.example.shoestore.data.model

data class User(
    val id: String,
    val aud: String,
    val role: String,
    val email: String,
    val email_confirmed_at: String?,
    val phone: String?,
    val confirmed_at: String?,
    val last_sign_in_at: String?,
    val app_metadata: AppMetadata,
    val identities: List<Identity>?,
    val created_at: String,
    val updated_at: String
)
