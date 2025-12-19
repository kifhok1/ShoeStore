package com.example.shoestore.data.model

import com.google.gson.annotations.SerializedName

data class ProfileUpdateDto(
    @SerializedName("firstname")
    val firstname: String? = null,

    @SerializedName("lastname")
    val lastname: String? = null,

    @SerializedName("address")
    val address: String? = null,

    @SerializedName("phone")
    val phone: String? = null,

    @SerializedName("photo")
    val photo: String? = null
)
