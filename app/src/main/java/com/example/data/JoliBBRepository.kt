package com.example.data

import kotlinx.coroutines.flow.Flow

class JoliBBRepository(
    private val productDao: ProductDao,
    private val cartItemDao: CartItemDao
) {
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()

    fun getProductById(id: Int): Flow<Product?> = productDao.getProductById(id)

    suspend fun insertProduct(product: Product) {
        productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }

    val cartItems: Flow<List<CartItem>> = cartItemDao.getCartItems()

    suspend fun insertCartItem(cartItem: CartItem) {
        cartItemDao.insertCartItem(cartItem)
    }

    suspend fun updateCartItem(cartItem: CartItem) {
        cartItemDao.updateCartItem(cartItem)
    }

    suspend fun deleteCartItem(cartItem: CartItem) {
        cartItemDao.deleteCartItem(cartItem)
    }

    suspend fun clearCart() {
        cartItemDao.clearCart()
    }
}
