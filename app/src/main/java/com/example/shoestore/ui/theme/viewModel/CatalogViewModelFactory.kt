package com.example.shoestore.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.shoestore.data.AuthStore

class CatalogViewModelFactory (
    private val authStore: AuthStore
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CatalogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CatalogViewModel(authStore) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: $modelClass")
    }
}