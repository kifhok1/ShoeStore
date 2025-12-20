package com.example.shoestore.ui.theme.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoestore.data.RetrofitInstance
import com.example.shoestore.data.model.CategoryDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel: ViewModel() {

    private val api = RetrofitInstance.catalogService

    private val _categories = MutableStateFlow<List<CategoryDto>>(emptyList())
    val categories: StateFlow<List<CategoryDto>> = _categories

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Добавили поле для ошибок
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                Log.d("HomeViewModel", "Начало загрузки категорий...")

                // Выполняем запрос на IO диспетчере
                val list = withContext(Dispatchers.IO) {
                    // 1. Делаем запрос
                    val dbCats = api.getCategories()

                    // ЛОГ: Смотрим, что пришло из базы
                    Log.d("HomeViewModel", "Пришло из Supabase: ${dbCats.size} категорий. Список: $dbCats")

                    // 2. Создаем категорию "Все"
                    val all = CategoryDto(id = "all", title = "Все") // Убедитесь, что в DTO поля совпадают с БД!

                    // 3. Объединяем
                    listOf(all) + dbCats
                }

                _categories.value = list
                Log.d("HomeViewModel", "Итоговый список в UI: ${list.map { it.title }}")

                // Логика выбора категории по умолчанию (Outdoor)
                // Добавил ignoreCase = true, чтобы найти "outdoor" или "OUTDOOR" тоже
                val start = list.indexOfFirst { it.title.equals("Outdoor", ignoreCase = true) }

                // Если Outdoor не найден, выбираем 0 (категорию "Все")
                _selectedIndex.value = if (start >= 0) start else 0

            } catch (e: Exception) {
                // ЛОГ: Если упала ошибка, выводим её полностью в консоль
                Log.e("HomeViewModel", "Ошибка загрузки категорий", e)
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCategory(index: Int) {
        _selectedIndex.value = index
    }

    // Метод для повторной попытки (можно вызывать из UI по кнопке)
    fun retry() {
        loadCategories()
    }
}
