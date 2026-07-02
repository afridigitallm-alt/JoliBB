package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.CartItem
import com.example.data.JoliBBRepository
import com.example.data.Product
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Accueil : Screen()
    object Catalogue : Screen()
    data class ProduitDetail(val productId: Int) : Screen()
    object Panier : Screen()
    object AdminLogin : Screen()
    object AdminDashboard : Screen()
}

data class FilterState(
    val category: String = "",
    val age: String = "",
    val sexe: String = "", // Garçon, Fille, Mixte
    val maxPriceXaf: Double = 50000.0,
    val color: String = "",
    val material: String = "",
    val size: String = "",
    val onlyNew: Boolean = false,
    val onlyPromo: Boolean = false,
    val searchQuery: String = ""
)

enum class SortType {
    PERTINENCE,
    PRIX_CROISSANT,
    PRIX_DECROISSANT,
    NOUVEAUTES
}

class JoliBBViewModel(private val repository: JoliBBRepository) : ViewModel() {

    // Text Resources for Easy Customization & Display
    val shortAccroches = listOf(
        "Douceur de coton pour votre trésor",
        "La plus douce layette à Douala",
        "Trousseau de naissance de rêve",
        "Coton bio certifié pour bébé",
        "Le confort absolu des nouveau-nés"
    )

    val longAccroches = listOf(
        "Découvrez notre collection de layettes douces et raffinées en coton bio, idéale pour habiller votre nouveau-né dès le premier jour.",
        "Layette JoliBB habille votre bébé de matières naturelles et saines, avec livraison rapide à domicile partout à Douala.",
        "Des coffrets de naissance élégants et des pyjamas douillets conçus pour prendre soin de la peau délicate de votre petit ange."
    )

    // Current Screen Routing
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Accueil)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Navigation Backstack (Simple list of previous screens)
    private val backstack = mutableListOf<Screen>()

    fun navigateTo(screen: Screen) {
        backstack.add(_currentScreen.value)
        _currentScreen.value = screen
    }

    fun navigateBack() {
        if (backstack.isNotEmpty()) {
            _currentScreen.value = backstack.removeAt(backstack.lastIndex)
        } else {
            _currentScreen.value = Screen.Accueil
        }
    }

    // Products from Room
    val allProducts: StateFlow<List<Product>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Cart Items from Room
    val cartItems: StateFlow<List<CartItem>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filters and Sorting States
    private val _filters = MutableStateFlow(FilterState())
    val filters: StateFlow<FilterState> = _filters.asStateFlow()

    private val _sortType = MutableStateFlow(SortType.PERTINENCE)
    val sortType: StateFlow<SortType> = _sortType.asStateFlow()

    // Filtered Products Flow
    val filteredProducts: StateFlow<List<Product>> = combine(allProducts, _filters, _sortType) { products, filter, sort ->
        var list = products.filter { product ->
            val matchesSearch = filter.searchQuery.isEmpty() ||
                    product.title.contains(filter.searchQuery, ignoreCase = true) ||
                    product.descriptionShort.contains(filter.searchQuery, ignoreCase = true) ||
                    product.sku.contains(filter.searchQuery, ignoreCase = true)

            val matchesCategory = filter.category.isEmpty() || 
                    product.categories.contains(filter.category, ignoreCase = true)

            val matchesAge = filter.age.isEmpty() || 
                    product.sizes.contains(filter.age, ignoreCase = true) || 
                    product.descriptionLong.contains(filter.age, ignoreCase = true)

            val matchesSexe = filter.sexe.isEmpty() || 
                    product.colors.contains(filter.sexe, ignoreCase = true) || 
                    product.tags.contains(filter.sexe, ignoreCase = true) ||
                    (filter.sexe == "Mixte" && (product.colors.contains("Blanc", ignoreCase = true) || product.colors.contains("Beige", ignoreCase = true) || product.colors.contains("Écru", ignoreCase = true))) ||
                    (filter.sexe == "Garçon" && (product.colors.contains("Bleu", ignoreCase = true) || product.colors.contains("Gris", ignoreCase = true))) ||
                    (filter.sexe == "Fille" && (product.colors.contains("Rose", ignoreCase = true) || product.colors.contains("Poudré", ignoreCase = true)))

            val matchesPrice = product.promoPriceXaf <= filter.maxPriceXaf

            val matchesColor = filter.color.isEmpty() || 
                    product.colors.contains(filter.color, ignoreCase = true)

            val matchesMaterial = filter.material.isEmpty() || 
                    product.material.contains(filter.material, ignoreCase = true)

            val matchesSize = filter.size.isEmpty() || 
                    product.sizes.contains(filter.size, ignoreCase = true)

            val matchesNew = !filter.onlyNew || 
                    product.tags.contains("Nouveauté", ignoreCase = true)

            val matchesPromo = !filter.onlyPromo || product.isPromo

            matchesSearch && matchesCategory && matchesAge && matchesSexe && matchesPrice && matchesColor && matchesMaterial && matchesSize && matchesNew && matchesPromo
        }

        // Apply Sorting
        list = when (sort) {
            SortType.PERTINENCE -> list
            SortType.PRIX_CROISSANT -> list.sortedBy { it.promoPriceXaf }
            SortType.PRIX_DECROISSANT -> list.sortedByDescending { it.promoPriceXaf }
            SortType.NOUVEAUTES -> list.sortedByDescending { it.id }
        }

        list
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Update individual filters
    fun updateSearchQuery(query: String) {
        _filters.value = _filters.value.copy(searchQuery = query)
    }

    fun setCategoryFilter(category: String) {
        _filters.value = _filters.value.copy(category = category)
    }

    fun setFilterState(newState: FilterState) {
        _filters.value = newState
    }

    fun setSortType(type: SortType) {
        _sortType.value = type
    }

    fun resetFilters() {
        _filters.value = FilterState()
        _sortType.value = SortType.PERTINENCE
    }

    // Auto-completion suggestions for Search
    val searchSuggestions: StateFlow<List<String>> = _filters.map { state ->
        val query = state.searchQuery.trim()
        if (query.length < 2) emptyList()
        else {
            val words = listOf("body", "pyjama", "bonnet", "moufle", "brassière", "nid d'ange", "cape de bain", "coffret", "chausson", "coton bio", "naissance")
            words.filter { it.contains(query, ignoreCase = true) }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // --- CART ACTIONS ---
    private val _isAddingToCart = MutableStateFlow(false)
    val isAddingToCart: StateFlow<Boolean> = _isAddingToCart.asStateFlow()

    fun addToCart(product: Product, size: String, color: String) {
        viewModelScope.launch {
            _isAddingToCart.value = true
            kotlinx.coroutines.delay(600) // Visual micro-interaction loader feedback

            val existingCart = cartItems.value
            val existingItem = existingCart.find {
                it.productId == product.id && it.selectedSize == size && it.selectedColor == color
            }

            if (existingItem != null) {
                existingItem.quantity += 1
                repository.updateCartItem(existingItem)
            } else {
                repository.insertCartItem(
                    CartItem(
                        productId = product.id,
                        title = product.title,
                        priceXaf = product.promoPriceXaf,
                        priceEur = product.promoPriceEur,
                        imagePath = product.imagePath,
                        selectedSize = size,
                        selectedColor = color,
                        quantity = 1
                    )
                )
            }
            _isAddingToCart.value = false
        }
    }

    fun updateCartItemQuantity(item: CartItem, delta: Int) {
        viewModelScope.launch {
            val newQty = item.quantity + delta
            if (newQty <= 0) {
                repository.deleteCartItem(item)
            } else {
                item.quantity = newQty
                repository.updateCartItem(item)
            }
        }
    }

    fun removeCartItem(item: CartItem) {
        viewModelScope.launch {
            repository.deleteCartItem(item)
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
        }
    }


    // --- DELIVERY AND CHECKOUT CALCULATOR ---
    val doualaNeighborhoods = listOf(
        Neighborhood("Retrait en boutique (Akwa)", 0.0, "À la boutique Layette JoliBB (Gratuit)"),
        Neighborhood("Akwa (Livraison locale)", 1000.0, "Livré en 2 à 4h à Douala 1er"),
        Neighborhood("Bonapriso (Livraison locale)", 1000.0, "Livré en 2 à 4h à Douala 1er"),
        Neighborhood("Deido (Livraison locale)", 1200.0, "Livré en 3 à 5h à Douala 1er"),
        Neighborhood("Bonamoussadi (Livraison locale)", 1500.0, "Livré en 4 à 8h à Douala 5e"),
        Neighborhood("Kotto (Livraison locale)", 1500.0, "Livré en 4 à 8h à Douala 5e"),
        Neighborhood("Bassa (Livraison locale)", 1500.0, "Livré en 4 à 8h à Douala 3e"),
        Neighborhood("Logbessou (Livraison locale)", 2000.0, "Livré en 6 à 12h à Douala 5e"),
        Neighborhood("Yassa (Livraison locale)", 2500.0, "Livré en 12 à 24h à Douala 3e"),
        Neighborhood("Expédition Internationale (DHL)", 15000.0, "Livré sous 3 à 5 jours ouvrés")
    )

    private val _selectedDelivery = MutableStateFlow(doualaNeighborhoods[1]) // Default Akwa delivery
    val selectedDelivery: StateFlow<Neighborhood> = _selectedDelivery.asStateFlow()

    fun selectDelivery(neighborhood: Neighborhood) {
        _selectedDelivery.value = neighborhood
    }

    val cartSubtotalXaf: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.totalXaf }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartSubtotalEur: StateFlow<Double> = cartItems.map { items ->
        items.sumOf { it.totalEur }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotalXaf: StateFlow<Double> = combine(cartSubtotalXaf, _selectedDelivery) { subtotal, delivery ->
        subtotal + delivery.feeXaf
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

    val cartTotalEur: StateFlow<Double> = combine(cartSubtotalEur, _selectedDelivery) { subtotal, delivery ->
        val deliveryEur = delivery.feeXaf / 655.95 // Direct peg conversion
        subtotal + deliveryEur
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)


    // Promo Code logic
    private val _promoCode = MutableStateFlow("")
    val promoCode: StateFlow<String> = _promoCode.asStateFlow()

    private val _promoDiscountRatio = MutableStateFlow(0.0) // 0.1 for 10%
    val promoDiscountRatio: StateFlow<Double> = _promoDiscountRatio.asStateFlow()

    private val _promoError = MutableStateFlow<String?>(null)
    val promoError: StateFlow<String?> = _promoError.asStateFlow()

    fun applyPromoCode(code: String) {
        if (code.equals("JOLIBB10", ignoreCase = true)) {
            _promoDiscountRatio.value = 0.10
            _promoCode.value = code.uppercase()
            _promoError.value = null
        } else if (code.equals("BIENVENUE20", ignoreCase = true)) {
            _promoDiscountRatio.value = 0.20
            _promoCode.value = code.uppercase()
            _promoError.value = null
        } else {
            _promoError.value = "Code promo non reconnu !"
            _promoDiscountRatio.value = 0.0
            _promoCode.value = ""
        }
    }


    // --- ADMINISTRATIVE AUTH & CONTROLS ---
    private val _adminLoggedIn = MutableStateFlow(false)
    val adminLoggedIn: StateFlow<Boolean> = _adminLoggedIn.asStateFlow()

    private val _adminError = MutableStateFlow<String?>(null)
    val adminError: StateFlow<String?> = _adminError.asStateFlow()

    fun loginAdmin(email: String, code: String): Boolean {
        if (email.trim() == "afridigitallm@gmail.com" && code.trim() == "103729") {
            _adminLoggedIn.value = true
            _adminError.value = null
            _currentScreen.value = Screen.AdminDashboard
            return true
        } else {
            _adminError.value = "Identifiants administratifs incorrects !"
            return false
        }
    }

    fun logoutAdmin() {
        _adminLoggedIn.value = false
        _currentScreen.value = Screen.Accueil
    }

    // Admin CRUD Operations
    fun adminDeleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
        }
    }

    fun adminSaveProduct(product: Product) {
        viewModelScope.launch {
            if (product.id == 0) {
                repository.insertProduct(product)
            } else {
                repository.updateProduct(product)
            }
        }
    }
}

data class Neighborhood(
    val name: String,
    val feeXaf: Double,
    val deliveryInfo: String
)

class JoliBBViewModelFactory(private val repository: JoliBBRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(JoliBBViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return JoliBBViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
