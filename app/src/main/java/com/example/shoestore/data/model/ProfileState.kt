package com.example.shoestore.data.model

sealed class ProfileState {
    object Loading : ProfileState()
    data class Ready(val profile: ProfileDto) : ProfileState()
    data class Saving(val profile: ProfileDto) : ProfileState()
    data class Error(val message: String) : ProfileState()
}