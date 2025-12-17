package com.example.shoestore.data.model

sealed class SignUpState {
    object Idle : SignUpState()
    object Success : SignUpState()
    data class Error(val message: String) : SignUpState()
}