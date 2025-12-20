package com.example.shoestore.ui.theme.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shoestore.data.AuthStore
import com.example.shoestore.data.RetrofitInstance
import com.example.shoestore.data.model.CategoryDto
import com.example.shoestore.data.model.FavouriteDto
import com.example.shoestore.data.model.ProductDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.SocketTimeoutException

private const val TAG = "CatalogViewModel" // Тэг для Logcat

open class CatalogViewModel(
    private val authStore: AuthStore
) : ViewModel() {

    private val api = RetrofitInstance.catalogService
    private val favouriteApi = RetrofitInstance.favouriteService

    // --- StateFlows для UI ---
    private val _categories = MutableStateFlow<List<CategoryDto>>(emptyList())
    val categories: StateFlow<List<CategoryDto>> = _categories

    private val _selectedIndex = MutableStateFlow(0)
    val selectedIndex: StateFlow<Int> = _selectedIndex

    private val _products = MutableStateFlow<List<ProductDto>>(emptyList())
    val products: StateFlow<List<ProductDto>> = _products

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _favouriteIds = MutableStateFlow<Set<String>>(emptySet())
    val favouriteIds: StateFlow<Set<String>> = _favouriteIds

    private val _favoriteProducts = MutableStateFlow<List<ProductDto>>(emptyList())
    val favoriteProducts: StateFlow<List<ProductDto>> = _favoriteProducts

    // --- Внутреннее состояние ---
    private var userId: String? = null
    private var pendingTitle: String? = null

    init {
        Log.d(TAG, "init() called")
        userId = authStore.getUserId()
        Log.d(TAG, "User ID loaded: $userId")

        if (userId != null) {
            loadFavourites()
        } else {
            Log.d(TAG, "User not authorized, skipping loadFavourites()")
        }

        loadCategories()
    }

    fun setInitialCategoryTitle(title: String) {
        Log.d(TAG, "setInitialCategoryTitle: $title")
        pendingTitle = title
        applyPendingTitleIfPossible()
    }

    private fun loadCategories() {
        Log.d(TAG, "loadCategories() started")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val dbCats = withContext(Dispatchers.IO) {
                    api.getCategories()
                }
                Log.d(TAG, "Categories loaded from API: ${dbCats.size}")

                val all = CategoryDto(id = "all", title = "Все")
                val fullList = listOf(all) + dbCats
                _categories.value = fullList

                val targetIndex = findIndexByTitle(pendingTitle)
                    ?: fullList.indexOfFirst { it.title.equals("Outdoor", true) }.takeIf { it >= 0 }
                    ?: 0

                Log.d(TAG, "Initial category index: $targetIndex (pendingTitle=$pendingTitle)")
                pendingTitle = null

                _selectedIndex.value = targetIndex
                loadProductsForCategory(targetIndex)

            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "loadCategories timeout", e)
                _error.value = "Превышен таймаут. Проверьте интернет."
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e(TAG, "loadCategories error", e)
                _error.value = "Ошибка загрузки: ${e.message}"
                _isLoading.value = false
            }
        }
    }

    fun onCategorySelected(index: Int) {
        Log.d(TAG, "onCategorySelected: index=$index")
        if (index == _selectedIndex.value && _products.value.isNotEmpty()) {
            Log.d(TAG, "Category already selected and products loaded. Skipping.")
            return
        }

        _selectedIndex.value = index
        loadProductsForCategory(index)
    }

    private fun loadProductsForCategory(index: Int) {
        val cat = _categories.value.getOrNull(index)
        Log.d(TAG, "loadProductsForCategory: ${cat?.title} (id=${cat?.id})")

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                _products.value = withContext(Dispatchers.IO) {
                    if (cat == null || cat.id == "all" || cat.title.equals("Все", true)) {
                        Log.d(TAG, "Fetching ALL products")
                        api.getProducts()
                    } else {
                        Log.d(TAG, "Fetching products for category ${cat.id}")
                        api.getProductsByCategoryId(categoryIdEq = "eq.${cat.id}")
                    }
                }
                Log.d(TAG, "Products loaded: ${_products.value.size}")

            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "loadProductsForCategory timeout", e)
                _error.value = "Превышен таймаут. Попробуйте еще раз."
            } catch (e: Exception) {
                Log.e(TAG, "loadProductsForCategory error", e)
                _error.value = "Ошибка загрузки продуктов: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun retry() {
        Log.d(TAG, "retry() called")
        if (_categories.value.isEmpty()) {
            loadCategories()
        } else {
            loadProductsForCategory(_selectedIndex.value)
        }
    }

    private fun applyPendingTitleIfPossible() {
        val title = pendingTitle ?: return
        if (_categories.value.isEmpty()) return

        Log.d(TAG, "applyPendingTitleIfPossible: applying $title")
        val idx = findIndexByTitle(title) ?: 0
        pendingTitle = null
        onCategorySelected(idx)
    }

    private fun findIndexByTitle(title: String?): Int? {
        val t = title?.trim().orEmpty()
        if (t.isEmpty()) return null
        return _categories.value.indexOfFirst { it.title.equals(t, ignoreCase = true) }
            .takeIf { it >= 0 }
    }

    // ---------- Работа с избранным ----------

    private fun loadFavourites() {
        val uid = userId ?: return
        Log.d(TAG, "loadFavourites() for user $uid")
        viewModelScope.launch {
            try {
                val favs = withContext(Dispatchers.IO) {
                    favouriteApi.getFavouritesByUser(userIdEq = "eq.$uid")
                }
                Log.d(TAG, "Favourites loaded: ${favs.size}")
                _favouriteIds.value = favs.mapNotNull { it.product_id }.toSet()
            } catch (e: Exception) {
                Log.e(TAG, "loadFavourites error", e)
            }
        }
    }

    fun toggleFavourite(productId: String) {
        val uid = userId
        if (uid == null) {
            Log.w(TAG, "toggleFavourite: User NOT authorized")
            return
        }

        val currentlyFav = _favouriteIds.value.contains(productId)
        Log.d(TAG, "toggleFavourite: product=$productId, currentlyFav=$currentlyFav")

        // 1. Оптимистичное обновление
        _favouriteIds.value =
            if (currentlyFav) _favouriteIds.value - productId
            else _favouriteIds.value + productId

        updateFavoriteProductsListLocal(productId, currentlyFav)

        // 2. API запрос
        viewModelScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    if (currentlyFav) {
                        Log.d(TAG, "API: Deleting favourite")
                        val response = favouriteApi.deleteFavourite(productIdEq = "eq.$productId", userIdEq = "eq.$uid")
                        if (!response.isSuccessful) {
                            throw Exception("Add fav failed: ${response.code()}")
                        }
                    } else {
                        Log.d(TAG, "API: Adding favourite")
                        favouriteApi.addFavourite(FavouriteDto(product_id = productId, user_id = uid))
                    }
                }
                Log.d(TAG, "API success")
            } catch (e: Exception) {
                Log.e(TAG, "API toggleFavourite failed, rolling back", e)
                // 3. Откат
                _favouriteIds.value =
                    if (currentlyFav) _favouriteIds.value + productId
                    else _favouriteIds.value - productId

                updateFavoriteProductsListLocal(productId, !currentlyFav)
            }
        }
    }

    private fun updateFavoriteProductsListLocal(productId: String, wasFavorite: Boolean) {
        if (wasFavorite) {
            _favoriteProducts.value = _favoriteProducts.value.filterNot { it.id == productId }
        } else {
            val product = _products.value.firstOrNull { it.id == productId }
            if (product != null && _favoriteProducts.value.none { it.id == productId }) {
                _favoriteProducts.value = _favoriteProducts.value + product
            }
        }
    }

    // ---------- Загрузка для экрана FavoriteScreen ----------

    fun loadFavoriteProducts() {
        val uid = userId
        if (uid == null) {
            Log.e(TAG, "loadFavoriteProducts: User ID is NULL. Cannot load favorites.")
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                // 1. Получаем список ID из таблицы связки
                Log.d(TAG, "Step 1: Fetching favorite IDs for user $uid")
                val favs = withContext(Dispatchers.IO) {
                    favouriteApi.getFavouritesByUser(userIdEq = "eq.$uid")
                }

                val favIds = favs.mapNotNull { it.product_id }.toSet()
                _favouriteIds.value = favIds
                Log.d(TAG, "Step 1 Result: Found ${favIds.size} IDs: $favIds")

                if (favIds.isEmpty()) {
                    _favoriteProducts.value = emptyList()
                    _isLoading.value = false
                    return@launch
                }

                // 2. Формируем строку запроса
                // Пример правильного формата: "in.(uuid1,uuid2)"
                val idParam = "in.(${favIds.joinToString(",")})"
                Log.d(TAG, "Step 2: Requesting products with query: id = $idParam")

                // 3. Загружаем сами товары
                val loadedProducts = withContext(Dispatchers.IO) {
                    api.getProductsByIds(idIn = idParam)
                }

                Log.d(TAG, "Step 3 Result: API returned ${loadedProducts.size} products")
                _favoriteProducts.value = loadedProducts

            } catch (e: Exception) {
                Log.e(TAG, "loadFavoriteProducts CRASH", e)
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun loadAllProductsIfNeeded() {
        if (_products.value.isNotEmpty()) {
            Log.d(TAG, "loadAllProductsIfNeeded: already loaded")
            return
        }

        Log.d(TAG, "loadAllProductsIfNeeded: loading...")
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                _products.value = withContext(Dispatchers.IO) {
                    api.getProducts()
                }
                Log.d(TAG, "loadAllProductsIfNeeded success: ${_products.value.size}")
            } catch (e: SocketTimeoutException) {
                Log.e(TAG, "loadAllProductsIfNeeded timeout", e)
                _error.value = "Превышен таймаут."
            } catch (e: Exception) {
                Log.e(TAG, "loadAllProductsIfNeeded error", e)
                _error.value = "Ошибка: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}
