package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: Int,
    val title: String,
    val priceXaf: Double,
    val priceEur: Double,
    val imagePath: String,
    val selectedSize: String,
    val selectedColor: String,
    var quantity: Int
) {
    val totalXaf: Double
        get() = priceXaf * quantity

    val totalEur: Double
        get() = priceEur * quantity
}
