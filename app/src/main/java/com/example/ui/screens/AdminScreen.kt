package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Product
import com.example.ui.JoliBBViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

// ==========================================
// ADMIN CONTAINER & ROUTING
// ==========================================
@Composable
fun AdminScreen(
    viewModel: JoliBBViewModel,
    modifier: Modifier = Modifier
) {
    val isLoggedIn by viewModel.adminLoggedIn.collectAsState()

    if (!isLoggedIn) {
        AdminLoginScreen(viewModel, modifier)
    } else {
        AdminDashboardScreen(viewModel, modifier)
    }
}


// ==========================================
// ADMIN LOGIN SCREEN
// ==========================================
@Composable
fun AdminLoginScreen(
    viewModel: JoliBBViewModel,
    modifier: Modifier = Modifier
) {
    var email by remember { mutableStateOf("afridigitallm@gmail.com") } // Pre-filled for convenience
    var password by remember { mutableStateOf("") }
    val errorMsg by viewModel.adminError.collectAsState()

    val context = LocalContext.current

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCream),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            border = BorderStroke(1.dp, BorderColor),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header lock icon
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(PastelRose),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Lock, contentDescription = "Security", tint = JoliBBPrimary, modifier = Modifier.size(32.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Connexion Administrative",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = JoliBBPrimary
                )
                Text(
                    text = "Espace de gestion Layette JoliBB",
                    fontSize = 12.sp,
                    color = TextLight,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Adresse email de l'admin") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JoliBBPrimary, unfocusedBorderColor = BorderColor)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Code d'accès secret") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JoliBBPrimary, unfocusedBorderColor = BorderColor)
                )

                if (errorMsg != null) {
                    Text(
                        text = errorMsg!!,
                        color = Color.Red,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Actions
                Button(
                    onClick = {
                        val success = viewModel.loginAdmin(email, password)
                        if (success) {
                            Toast.makeText(context, "Bienvenue Administrateur !", Toast.LENGTH_SHORT).show()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftCoral),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                ) {
                    Text("Se connecter au Dashboard", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { viewModel.navigateTo(Screen.Accueil) }) {
                    Text("Retour au site public", color = JoliBBPrimary)
                }
            }
        }
    }
}


// ==========================================
// ADMIN DASHBOARD
// ==========================================
@Composable
fun AdminDashboardScreen(
    viewModel: JoliBBViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.allProducts.collectAsState()
    var editingProduct by remember { mutableStateOf<Product?>(null) }
    var showFormDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCream)
    ) {
        // Admin Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(TextDark)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Outlined.AdminPanelSettings, contentDescription = "Admin", tint = JoliBBPrimary)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text("Dashboard JoliBB", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 16.sp)
                    Text("Connecté : afridigitallm@gmail.com", color = Color.LightGray, fontSize = 10.sp)
                }
            }

            Row {
                Button(
                    onClick = {
                        editingProduct = Product(
                            title = "",
                            sku = "JBB-NEW-" + (100..999).random(),
                            priceXaf = 10000.0,
                            priceEur = 15.20,
                            descriptionShort = "",
                            descriptionLong = "",
                            imagePath = "img_logo",
                            categories = "Layette Naissance",
                            tags = "Nouveauté",
                            stock = 10,
                            sizes = "Naissance, 1 mois, 3 mois",
                            colors = "Blanc, Rose, Bleu",
                            material = "100% Coton Biologique",
                            entretien = "Lavage machine 30°C"
                        )
                        showFormDialog = true
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JoliBBPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ajouter", fontSize = 11.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(onClick = { viewModel.logoutAdmin() }) {
                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.LightGray)
                }
            }
        }

        // Product Catalog Management List
        Text(
            text = "Gestion du Catalogue (${products.size} articles)",
            fontWeight = FontWeight.Bold,
            fontSize = 15.sp,
            color = TextDark,
            modifier = Modifier.padding(16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(products) { product ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, BorderColor)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Product image placeholder representation
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(PastelRose),
                            contentAlignment = Alignment.Center
                        ) {
                            val (vector, tint) = getProductIconAndColor(product.title)
                            Icon(imageVector = vector, contentDescription = "Icon", tint = tint, modifier = Modifier.size(24.dp))
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        // Info column
                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, color = TextDark)
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("SKU: ${product.sku}", fontSize = 10.sp, color = TextLight)
                                Text("Stock: ${product.stock}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (product.stock > 0) SuccessGreen else SoftCoral)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = String.format("%,.0f XAF", product.promoPriceXaf),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = JoliBBPrimary
                                )
                                if (product.isPromo) {
                                    Text("Promo -${product.promoDiscountPercent}%", color = SoftCoral, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // CRUD Action buttons
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            IconButton(onClick = {
                                editingProduct = product
                                showFormDialog = true
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = JoliBBSecondary, modifier = Modifier.size(20.dp))
                            }
                            IconButton(onClick = {
                                viewModel.adminDeleteProduct(product)
                                Toast.makeText(context, "Article supprimé !", Toast.LENGTH_SHORT).show()
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SoftCoral, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Interactive Edit/Add product dialog
    if (showFormDialog && editingProduct != null) {
        var title by remember { mutableStateOf(editingProduct!!.title) }
        var sku by remember { mutableStateOf(editingProduct!!.sku) }
        var categories by remember { mutableStateOf(editingProduct!!.categories) }
        var tags by remember { mutableStateOf(editingProduct!!.tags) }
        var priceXaf by remember { mutableStateOf(editingProduct!!.priceXaf.toString()) }
        var priceEur by remember { mutableStateOf(editingProduct!!.priceEur.toString()) }
        var descShort by remember { mutableStateOf(editingProduct!!.descriptionShort) }
        var descLong by remember { mutableStateOf(editingProduct!!.descriptionLong) }
        var stock by remember { mutableStateOf(editingProduct!!.stock.toString()) }
        var isPromo by remember { mutableStateOf(editingProduct!!.isPromo) }
        var discountPercent by remember { mutableStateOf(editingProduct!!.promoDiscountPercent.toString()) }

        var material by remember { mutableStateOf(editingProduct!!.material) }
        var entretien by remember { mutableStateOf(editingProduct!!.entretien) }
        var sizes by remember { mutableStateOf(editingProduct!!.sizes) }
        var colors by remember { mutableStateOf(editingProduct!!.colors) }

        AlertDialog(
            onDismissRequest = { showFormDialog = false },
            confirmButton = {
                Button(
                    colors = ButtonDefaults.buttonColors(containerColor = JoliBBPrimary),
                    onClick = {
                        if (title.isEmpty()) {
                            Toast.makeText(context, "Le titre est requis !", Toast.LENGTH_SHORT).show()
                            return@Button
                        }

                        val parsedPriceXaf = priceXaf.toDoubleOrNull() ?: 10000.0
                        val parsedPriceEur = priceEur.toDoubleOrNull() ?: (parsedPriceXaf / 655.95)

                        val updated = editingProduct!!.copy(
                            title = title,
                            sku = sku,
                            categories = categories,
                            tags = tags,
                            priceXaf = parsedPriceXaf,
                            priceEur = parsedPriceEur,
                            descriptionShort = descShort,
                            descriptionLong = descLong,
                            stock = stock.toIntOrNull() ?: 10,
                            isPromo = isPromo,
                            promoDiscountPercent = discountPercent.toIntOrNull() ?: 0,
                            material = material,
                            entretien = entretien,
                            sizes = sizes,
                            colors = colors
                        )

                        viewModel.adminSaveProduct(updated)
                        showFormDialog = false
                        Toast.makeText(context, "Article enregistré avec succès !", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("Enregistrer")
                }
            },
            dismissButton = {
                TextButton(onClick = { showFormDialog = false }) {
                    Text("Annuler", color = JoliBBPrimary)
                }
            },
            title = {
                Text(
                    text = if (editingProduct!!.id == 0) "Ajouter un article" else "Modifier l'article",
                    fontWeight = FontWeight.Bold,
                    color = JoliBBPrimary,
                    fontSize = 16.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .height(400.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Title
                    OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Titre de l'article") })
                    // SKU
                    OutlinedTextField(value = sku, onValueChange = { sku = it }, label = { Text("SKU unique") })

                    // Category input
                    OutlinedTextField(
                        value = categories,
                        onValueChange = { categories = it },
                        label = { Text("Catégories (séparées par virgules)") },
                        placeholder = { Text("Ex: Layette Naissance, Vêtements 0-3 mois") }
                    )

                    // Tags input
                    OutlinedTextField(
                        value = tags,
                        onValueChange = { tags = it },
                        label = { Text("Tags (séparés par virgules)") },
                        placeholder = { Text("Ex: coton bio, nouveauté, promo") }
                    )

                    // Price XAF & EUR
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = priceXaf,
                            onValueChange = { priceXaf = it },
                            label = { Text("Prix XAF") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = priceEur,
                            onValueChange = { priceEur = it },
                            label = { Text("Prix EUR") },
                            modifier = Modifier.weight(1f),
                            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                    }

                    // Stock
                    OutlinedTextField(
                        value = stock,
                        onValueChange = { stock = it },
                        label = { Text("Stock disponible") },
                        keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                    )

                    // Promotion section
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Checkbox(
                            checked = isPromo,
                            onCheckedChange = { isPromo = it },
                            colors = CheckboxDefaults.colors(checkedColor = JoliBBPrimary)
                        )
                        Text("En promotion active")
                    }

                    AnimatedVisibility(visible = isPromo) {
                        OutlinedTextField(
                            value = discountPercent,
                            onValueChange = { discountPercent = it },
                            label = { Text("Pourcentage de réduction %") },
                            keyboardOptions = KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
                        )
                    }

                    // Descriptions
                    OutlinedTextField(value = descShort, onValueChange = { descShort = it }, label = { Text("Description courte (1 ligne)") })
                    OutlinedTextField(value = descLong, onValueChange = { descLong = it }, label = { Text("Description longue (3-4 phrases)") }, minLines = 3)

                    // Material / Entretien
                    OutlinedTextField(value = material, onValueChange = { material = it }, label = { Text("🌿 Matière / Tissu") })
                    OutlinedTextField(value = entretien, onValueChange = { entretien = it }, label = { Text("🧼 Entretien (Lavage...)") })

                    // Sizes and Colors list
                    OutlinedTextField(value = sizes, onValueChange = { sizes = it }, label = { Text("Talle(s) disponible(s) (virgules)") })
                    OutlinedTextField(value = colors, onValueChange = { colors = it }, label = { Text("Couleur(s) disponible(s) (virgules)") })

                    // Image simulation banner
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("📷 Photo du produit", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextLight)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(BorderColor)
                            .clickable {
                                Toast
                                    .makeText(context, "Simulation de capture / sélection de photo effectuée !", Toast.LENGTH_SHORT)
                                    .show()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudUpload, contentDescription = "Upload", tint = JoliBBPrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Simuler l'upload d'image (Galerie / Caméra)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = JoliBBPrimary)
                        }
                    }
                }
            }
        )
    }
}
