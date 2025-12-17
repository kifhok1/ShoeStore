package com.example.shoestore.data.model

data class Identity(
    val identity_id: String,
    val id: String,
    val user_id: String,
    val identity_data: IdentityData,
    val provider: String,
    val last_sign_in_at: String,
    val created_at: String,
    val updated_at: String
)
