package com.example.shoestore.data.model

sealed class ProfileState {
    data object Loading : ProfileState()
    data class Ready(val profile: ProfileDto) : ProfileState()
    data class Error(val message: String) : ProfileState()
}