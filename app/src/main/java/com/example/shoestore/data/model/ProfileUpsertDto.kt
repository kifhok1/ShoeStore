package com.example.shoestore.data.model

data class ProfileUpsertDto(
    val user_id: String,
    val photo: String? = null,
    val firstname: String? = null,
    val lastname: String? = null,
    val address: String? = null,
    val phone: String? = null
)
