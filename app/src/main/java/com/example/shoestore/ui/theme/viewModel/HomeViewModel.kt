package com.example.shoestore.ui.theme.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoestore.data.RetrofitInstance
import com.example.shoestore.data.model.CategoryDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val api = RetrofitInstance.catalogService

    private val _categories = MutableStateFlow<List<CategoryDto>>(emptyList())
    val categories: StateFlow<List<CategoryDto>> = _categories

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            runCatching {
                val dbCats = api.getCategories() // Tennis/Men/Women/Outdoor
                val all = CategoryDto(id = "all", title = "Все")
                listOf(all) + dbCats
            }.onSuccess { list ->
                _categories.value = list

                // по умолчанию подсветим Outdoor если есть
                val start = list.indexOfFirst { it.title.equals("Outdoor", true) }
                    .takeIf { it >= 0 } ?: 0
                _selectedIndex.value = start
            }.onFailure {
                it.printStackTrace()
            }
            _isLoading.value = false
        }
    }

    fun selectCategory(index: Int) {
        _selectedIndex.value = index
    }
}