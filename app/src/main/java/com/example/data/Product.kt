package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val sku: String,
    val priceXaf: Double,
    val priceEur: Double,
    val descriptionShort: String,
    val descriptionLong: String,
    val imagePath: String, // Can be "img_logo", "img_hero_banner", or a local URI / custom drawable path
    val categories: String, // Comma-separated categories: Layette Naissance, Vêtements 0–3 mois, Accessoires, Cadeaux naissance
    val tags: String, // Comma-separated: coton bio, promo, nouveauté, best-seller
    val stock: Int,
    val sizes: String, // Comma-separated: Naissance, 1 mois, 3 mois
    val colors: String, // Comma-separated: Blanc, Rose pâle, Bleu ciel, Beige
    val material: String, // e.g. "100% Coton Biologique"
    val entretien: String, // e.g. "Lavage machine à 30°C"
    val rating: Float = 4.8f,
    val reviewCount: Int = 10,
    val isPromo: Boolean = false,
    val promoDiscountPercent: Int = 0
) {
    val promoPriceXaf: Double
        get() = if (isPromo) priceXaf * (1 - promoDiscountPercent / 100.0) else priceXaf

    val promoPriceEur: Double
        get() = if (isPromo) priceEur * (1 - promoDiscountPercent / 100.0) else priceEur

    fun getCategoryList(): List<String> = categories.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    fun getTagList(): List<String> = tags.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    fun getSizeList(): List<String> = sizes.split(",").map { it.trim() }.filter { it.isNotEmpty() }
    fun getColorList(): List<String> = colors.split(",").map { it.trim() }.filter { it.isNotEmpty() }
}
