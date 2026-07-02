package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.CartItem
import com.example.data.Product
import com.example.ui.FilterState
import com.example.ui.JoliBBViewModel
import com.example.ui.Neighborhood
import com.example.ui.Screen
import com.example.ui.SortType
import com.example.ui.theme.*
import java.net.URLEncoder

// --- CURRENCY TOGGLE HELPER ---
private val showEurState = mutableStateOf(false)

@Composable
fun CurrencyToggle(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(BorderColor)
            .clickable { showEurState.value = !showEurState.value }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.CurrencyExchange,
            contentDescription = "Change Currency",
            tint = JoliBBPrimary,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (showEurState.value) "Devise : EUR (€)" else "Devise : XAF (FCFA)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextDark
        )
    }
}

@Composable
fun formatPrice(xaf: Double, eur: Double): String {
    return if (showEurState.value) {
        String.format("%.2f €", eur)
    } else {
        String.format("%,.0f XAF", xaf)
    }
}


// ==========================================
// 1. HOME SCREEN (PAGE D'ACCUEIL)
// ==========================================
@Composable
fun HomeScreen(
    viewModel: JoliBBViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.allProducts.collectAsState()
    val scrollState = rememberScrollState()

    // Slide-show variables for text variants
    var shortHookIndex by remember { mutableStateOf(0) }
    var longHookIndex by remember { mutableStateOf(0) }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(WarmCream)
    ) {
        // Hero Section with generated banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = "Collection Layette JoliBB",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            // Overlay gradient for text readability
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)),
                            startY = 100f
                        )
                    )
            )

            // Hero content
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                // Interactive Badge to see the 5 short hooks requested
                Row(
                    modifier = Modifier
                        .clickable { shortHookIndex = (shortHookIndex + 1) % viewModel.shortAccroches.size }
                        .clip(RoundedCornerShape(12.dp))
                        .background(JoliBBPrimary)
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "👶 ${viewModel.shortAccroches[shortHookIndex]}",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Icon(Icons.Default.Refresh, contentDescription = "Next hook", tint = Color.White, modifier = Modifier.size(10.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Clicking changes the long hook text variant
                Text(
                    text = viewModel.longAccroches[longHookIndex],
                    color = Color.White,
                    fontSize = 15.sp,
                    lineHeight = 19.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .clickable { longHookIndex = (longHookIndex + 1) % viewModel.longAccroches.size }
                        .padding(bottom = 12.dp)
                )

                Button(
                    onClick = {
                        viewModel.resetFilters()
                        viewModel.navigateTo(Screen.Catalogue)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SoftCoral),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Découvrir la collection", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(Icons.Default.ArrowForward, contentDescription = "Arrow")
                    }
                }
            }
        }

        // Global Utility Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Offres Exclusives Douala", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
            CurrencyToggle()
        }

        // Blocs Promotionnels Section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Promo 1: Pack naissance
            Card(
                colors = CardDefaults.cardColors(containerColor = PastelRose),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        viewModel.resetFilters()
                        viewModel.setFilterState(FilterState(searchQuery = "Pack"))
                        viewModel.navigateTo(Screen.Catalogue)
                    },
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("📦 Pack Naissance", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = JoliBBPrimary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Trousseau indispensable de maternité tout-en-un.", fontSize = 11.sp, color = TextLight, lineHeight = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Voir les packs ›", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = JoliBBPrimary)
                }
            }

            // Promo 2: Discount
            Card(
                colors = CardDefaults.cardColors(containerColor = PowderBlue),
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        viewModel.resetFilters()
                        viewModel.setFilterState(FilterState(onlyPromo = true))
                        viewModel.navigateTo(Screen.Catalogue)
                    },
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("🎁 Promo JoliBB", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF1E88E5))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Bénéficiez de -20% sur nos articles signalés.", fontSize = 11.sp, color = TextLight, lineHeight = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("En profiter ›", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(0xFF1E88E5))
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Best Sellers Section
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🏆 Nos Best-Sellers",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = TextDark
                )
                Text(
                    text = "Voir tout",
                    fontSize = 13.sp,
                    color = JoliBBPrimary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        viewModel.resetFilters()
                        viewModel.navigateTo(Screen.Catalogue)
                    }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontally Scrollable list of Best Sellers
            val bestSellers = products.filter { it.tags.contains("Best-seller", ignoreCase = true) }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                if (bestSellers.isEmpty()) {
                    // Fallback to first few products if Room database is empty initially
                    products.take(3).forEach { product ->
                        ProductCompactCard(product, viewModel)
                    }
                } else {
                    bestSellers.forEach { product ->
                        ProductCompactCard(product, viewModel)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Localized Clinic / "À propos" brand card
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            border = BorderBorderStroke()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "🏠 À propos de Layette JoliBB",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = JoliBBPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Layette JoliBB est née de notre passion pour la layette douce et de qualité. Notre boutique est stratégiquement installée à Akwa, Douala, tout près de la Clinique de la Maternité, pour servir au mieux les futures mamans de la capitale économique du Cameroun.",
                    fontSize = 12.sp,
                    color = TextDark,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Maps button
                Button(
                    onClick = {
                        val mapIntent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://maps.google.com/?q=Akwa,Douala,Cameroun")
                        )
                        context.startActivity(mapIntent)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = JoliBBSecondary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Maps", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Nous localiser sur Google Maps", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun BorderBorderStroke() = BorderStroke(1.dp, BorderColor)


// ==========================================
// COMPACT CARD COMPONENT FOR ROW
// ==========================================
@Composable
fun ProductCompactCard(product: Product, viewModel: JoliBBViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .width(160.dp)
            .clickable { viewModel.navigateTo(Screen.ProduitDetail(product.id)) }
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(PastelRose),
                contentAlignment = Alignment.Center
            ) {
                // Since this is a mock we can render a beautiful Vector icon based on product title
                val (vector, colorTint) = getProductIconAndColor(product.title)
                Icon(
                    imageVector = vector,
                    contentDescription = product.title,
                    tint = colorTint,
                    modifier = Modifier.size(54.dp)
                )

                // Badge Nouveauté or Promo
                if (product.isPromo) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SoftCoral)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("-${product.promoDiscountPercent}%", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (product.tags.contains("Nouveauté", ignoreCase = true)) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(6.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SuccessGreen)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("New", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(8.dp)) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextDark
                )
                Text(
                    text = product.descriptionShort,
                    fontSize = 10.sp,
                    color = TextLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Price display
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (product.isPromo) {
                        Text(
                            text = formatPrice(product.promoPriceXaf, product.promoPriceEur),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = SoftCoral
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = formatPrice(product.priceXaf, product.priceEur),
                            fontSize = 9.sp,
                            color = TextLight,
                            textDecoration = TextDecoration.LineThrough
                        )
                    } else {
                        Text(
                            text = formatPrice(product.priceXaf, product.priceEur),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            color = TextDark
                        )
                    }
                }
            }
        }
    }
}


// Helper for mock product vector representations
fun getProductIconAndColor(title: String): Pair<androidx.compose.ui.graphics.vector.ImageVector, Color> {
    return when {
        title.contains("Kit", ignoreCase = true) || title.contains("Coffret", ignoreCase = true) -> Pair(Icons.Default.CardGiftcard, JoliBBPrimary)
        title.contains("Body", ignoreCase = true) -> Pair(Icons.Default.Checkroom, JoliBBSecondary)
        title.contains("Pyjama", ignoreCase = true) || title.contains("Brassière", ignoreCase = true) -> Pair(Icons.Default.HotelClass, JoliBBPrimary)
        title.contains("Gigoteuse", ignoreCase = true) || title.contains("Nid", ignoreCase = true) -> Pair(Icons.Default.Bed, Color(0xFF81C784))
        title.contains("Bonnet", ignoreCase = true) || title.contains("Chausson", ignoreCase = true) -> Pair(Icons.Default.Toys, Color(0xFFFFB74D))
        else -> Pair(Icons.Default.LocalMall, JoliBBPrimary)
    }
}


// ==========================================
// 2. CATALOG SCREEN (PAGE CATALOGUE AVEC FILTRES / TRIS)
// ==========================================
@Composable
fun CatalogScreen(
    viewModel: JoliBBViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.filteredProducts.collectAsState()
    val filterState by viewModel.filters.collectAsState()
    val sortType by viewModel.sortType.collectAsState()

    var showFilterSheet by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isTablet = configuration.screenWidthDp >= 600

    Row(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCream)
    ) {
        // Tablet Side Filters
        if (isTablet) {
            Column(
                modifier = Modifier
                    .width(260.dp)
                    .fillMaxHeight()
                    .background(Color.White)
                    .border(1.dp, BorderColor)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                FiltersContent(viewModel, filterState)
            }
        }

        // Main Catalog Section
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(16.dp)
        ) {
            // Header catalog row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (filterState.category.isNotEmpty()) filterState.category else "Tous nos articles",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = TextDark
                    )
                    Text(
                        text = "${products.size} articles trouvés",
                        fontSize = 11.sp,
                        color = TextLight
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CurrencyToggle()

                    if (!isTablet) {
                        Button(
                            onClick = { showFilterSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = JoliBBPrimary),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filtres", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Filtres", fontSize = 12.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sort Selector Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Trier par :", fontSize = 12.sp, color = TextLight)
                SortType.values().forEach { type ->
                    val isSelected = sortType == type
                    Text(
                        text = when (type) {
                            SortType.PERTINENCE -> "Pertinence"
                            SortType.PRIX_CROISSANT -> "Prix ↗"
                            SortType.PRIX_DECROISSANT -> "Prix ↘"
                            SortType.NOUVEAUTES -> "Nouveautés"
                        },
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) JoliBBPrimary else TextLight,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) PastelRose else Color.Transparent)
                            .clickable { viewModel.setSortType(type) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Product Grid
            if (products.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SearchOff, contentDescription = "Aucun produit", tint = TextLight, modifier = Modifier.size(64.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Aucun article ne correspond à vos filtres.", color = TextLight, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = { viewModel.resetFilters() }) {
                            Text("Réinitialiser les filtres", color = JoliBBPrimary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(if (isTablet) 3 else 2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(products) { product ->
                        ProductGridItem(product, viewModel)
                    }
                }
            }
        }
    }

    // Mobile Bottom Sheet Dialog Filters
    if (showFilterSheet && !isTablet) {
        AlertDialog(
            onDismissRequest = { showFilterSheet = false },
            confirmButton = {
                Button(onClick = { showFilterSheet = false }, colors = ButtonDefaults.buttonColors(containerColor = JoliBBPrimary)) {
                    Text("Appliquer")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.resetFilters()
                    showFilterSheet = false
                }) {
                    Text("Réinitialiser", color = JoliBBPrimary)
                }
            },
            title = { Text("Filtrer les articles", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = JoliBBPrimary) },
            text = {
                Box(modifier = Modifier.height(350.dp).verticalScroll(rememberScrollState())) {
                    FiltersContent(viewModel, filterState)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun FiltersContent(viewModel: JoliBBViewModel, filterState: FilterState) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Search query indicator
        if (filterState.searchQuery.isNotEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(PastelRose)
                    .padding(8.dp)
            ) {
                Text("Mot-clé : \"${filterState.searchQuery}\"", fontSize = 12.sp, color = TextDark, modifier = Modifier.weight(1f))
                IconButton(onClick = { viewModel.updateSearchQuery("") }, modifier = Modifier.size(16.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = JoliBBPrimary, modifier = Modifier.size(12.dp))
                }
            }
        }

        // Categories selector
        Text("Catégorie", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
        val cats = listOf("Layette Naissance", "Vêtements 0-3 mois", "Accessoires", "Cadeaux naissance")
        Column {
            cats.forEach { cat ->
                val isSel = filterState.category.equals(cat, ignoreCase = true)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setCategoryFilter(if (isSel) "" else cat) }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = isSel,
                        onClick = { viewModel.setCategoryFilter(if (isSel) "" else cat) },
                        colors = RadioButtonDefaults.colors(selectedColor = JoliBBPrimary)
                    )
                    Text(cat, fontSize = 13.sp, color = if (isSel) JoliBBPrimary else TextDark)
                }
            }
        }

        HorizontalDivider(color = BorderColor)

        // Sexe filter (Garçon, Fille, Mixte)
        Text("Sexe", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val sexes = listOf("Garçon", "Fille", "Mixte")
            sexes.forEach { sex ->
                val isSel = filterState.sexe == sex
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (isSel) JoliBBPrimary else BorderColor)
                        .clickable { viewModel.setFilterState(filterState.copy(sexe = if (isSel) "" else sex)) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(sex, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) Color.White else TextDark)
                }
            }
        }

        HorizontalDivider(color = BorderColor)

        // Age filter (naissance, 0-1 mois)
        Text("Âge", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
        Column {
            val ages = listOf("Naissance", "1 mois", "3 mois", "6-12 mois")
            ages.forEach { age ->
                val isSel = filterState.age == age
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setFilterState(filterState.copy(age = if (isSel) "" else age)) }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = isSel,
                        onCheckedChange = { viewModel.setFilterState(filterState.copy(age = if (isSel) "" else age)) },
                        colors = CheckboxDefaults.colors(checkedColor = JoliBBPrimary)
                    )
                    Text(age, fontSize = 13.sp, color = TextDark)
                }
            }
        }

        HorizontalDivider(color = BorderColor)

        // Price slider Max
        Text("Prix maximum (FCFA)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
        Column {
            Text("${String.format("%,.0f XAF", filterState.maxPriceXaf)}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = JoliBBPrimary)
            Slider(
                value = filterState.maxPriceXaf.toFloat(),
                onValueChange = { viewModel.setFilterState(filterState.copy(maxPriceXaf = it.toDouble())) },
                valueRange = 4000f..50000f,
                colors = SliderDefaults.colors(thumbColor = JoliBBPrimary, activeTrackColor = JoliBBPrimary)
            )
        }

        HorizontalDivider(color = BorderColor)

        // Matière
        Text("Matière", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
        Column {
            val mats = listOf("Coton Biologique", "Velours", "Tricot", "Cuir")
            mats.forEach { mat ->
                val isSel = filterState.material.contains(mat, ignoreCase = true)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setFilterState(filterState.copy(material = if (isSel) "" else mat)) }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = isSel,
                        onCheckedChange = { viewModel.setFilterState(filterState.copy(material = if (isSel) "" else mat)) },
                        colors = CheckboxDefaults.colors(checkedColor = JoliBBPrimary)
                    )
                    Text(mat, fontSize = 13.sp)
                }
            }
        }

        HorizontalDivider(color = BorderColor)

        // Promos & Nouveautés Toggles
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Switch(
                checked = filterState.onlyPromo,
                onCheckedChange = { viewModel.setFilterState(filterState.copy(onlyPromo = it)) },
                colors = SwitchDefaults.colors(checkedThumbColor = JoliBBPrimary, checkedTrackColor = PastelRose)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("En promotion %", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Switch(
                checked = filterState.onlyNew,
                onCheckedChange = { viewModel.setFilterState(filterState.copy(onlyNew = it)) },
                colors = SwitchDefaults.colors(checkedThumbColor = JoliBBPrimary, checkedTrackColor = PastelRose)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Nouveautés ✨", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }
    }
}


// ==========================================
// GRID ITEM CARD COMPONENT
// ==========================================
@Composable
fun ProductGridItem(product: Product, viewModel: JoliBBViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { viewModel.navigateTo(Screen.ProduitDetail(product.id)) }
            .shadow(2.dp, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(PastelRose),
                contentAlignment = Alignment.Center
            ) {
                val (vector, tint) = getProductIconAndColor(product.title)
                Icon(imageVector = vector, contentDescription = product.title, tint = tint, modifier = Modifier.size(58.dp))

                // Badges
                if (product.isPromo) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SoftCoral)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("-${product.promoDiscountPercent}%", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                } else if (product.tags.contains("Nouveauté", ignoreCase = true)) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(SuccessGreen)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("New", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = product.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = TextDark
                )
                Text(
                    text = product.descriptionShort,
                    fontSize = 10.sp,
                    color = TextLight,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))

                // Price Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (product.isPromo) {
                        Text(
                            text = formatPrice(product.promoPriceXaf, product.promoPriceEur),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = SoftCoral
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formatPrice(product.priceXaf, product.priceEur),
                            fontSize = 10.sp,
                            color = TextLight,
                            textDecoration = TextDecoration.LineThrough
                        )
                    } else {
                        Text(
                            text = formatPrice(product.priceXaf, product.priceEur),
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 14.sp,
                            color = TextDark
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Double CTA: Ver / Ajouter
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.navigateTo(Screen.ProduitDetail(product.id)) },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = JoliBBPrimary),
                        border = BorderStroke(1.dp, JoliBBPrimary),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(34.dp)
                    ) {
                        Text("Voir", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = {
                            val defaultSize = product.getSizeList().firstOrNull() ?: "Naissance"
                            val defaultColor = product.getColorList().firstOrNull() ?: "Blanc"
                            viewModel.addToCart(product, defaultSize, defaultColor)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftCoral),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(34.dp)
                    ) {
                        Text("+ Panier", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}


// ==========================================
// 3. PRODUCT DETAIL SCREEN (FICHE PRODUIT)
// ==========================================
@Composable
fun DetailScreen(
    productId: Int,
    viewModel: JoliBBViewModel,
    modifier: Modifier = Modifier
) {
    val products by viewModel.allProducts.collectAsState()
    val isAdding by viewModel.isAddingToCart.collectAsState()
    val product = products.find { it.id == productId }

    if (product == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Article non trouvé !")
        }
        return
    }

    var selectedSize by remember(product) { mutableStateOf(product.getSizeList().firstOrNull() ?: "Naissance") }
    var selectedColor by remember(product) { mutableStateOf(product.getColorList().firstOrNull() ?: "Blanc") }
    var showSizeGuide by remember { mutableStateOf(false) }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(WarmCream)
    ) {
        // Back Navigation Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = JoliBBPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Retour", color = JoliBBPrimary, fontWeight = FontWeight.Bold)
            }
            CurrencyToggle()
        }

        // Adaptive Layout Split (Tablet-friendly side-by-side)
        val configuration = LocalConfiguration.current
        val isTablet = configuration.screenWidthDp >= 600

        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Image gallery side
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(380.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PastelRose),
                    contentAlignment = Alignment.Center
                ) {
                    val (vector, tint) = getProductIconAndColor(product.title)
                    Icon(imageVector = vector, contentDescription = product.title, tint = tint, modifier = Modifier.size(120.dp))
                }

                // Options side
                Column(modifier = Modifier.weight(1.2f)) {
                    ProductMainDetails(
                        product, selectedSize, selectedColor,
                        onSizeSelect = { selectedSize = it },
                        onColorSelect = { selectedColor = it },
                        onSizeGuideClick = { showSizeGuide = true },
                        onAddToCart = { viewModel.addToCart(product, selectedSize, selectedColor) },
                        isAdding = isAdding
                    )
                }
            }
        } else {
            // Mobile layout
            // Main Product Image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .background(PastelRose),
                contentAlignment = Alignment.Center
            ) {
                val (vector, tint) = getProductIconAndColor(product.title)
                Icon(imageVector = vector, contentDescription = product.title, tint = tint, modifier = Modifier.size(100.dp))
            }

            Column(modifier = Modifier.padding(16.dp)) {
                ProductMainDetails(
                    product, selectedSize, selectedColor,
                    onSizeSelect = { selectedSize = it },
                    onColorSelect = { selectedColor = it },
                    onSizeGuideClick = { showSizeGuide = true },
                    onAddToCart = { viewModel.addToCart(product, selectedSize, selectedColor) },
                    isAdding = isAdding
                )
            }
        }

        // Reviews and Suggestions sections
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(color = BorderColor, modifier = Modifier.padding(horizontal = 16.dp))
        Spacer(modifier = Modifier.height(16.dp))

        // "Vous aimerez aussi" section
        Column(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = "🎁 Vous aimerez aussi",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TextDark,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            val suggestions = products.filter { it.id != product.id }.take(3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                suggestions.forEach { sug ->
                    ProductCompactCard(sug, viewModel)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Size Guide Dialogue Popup
    if (showSizeGuide) {
        AlertDialog(
            onDismissRequest = { showSizeGuide = false },
            confirmButton = {
                Button(onClick = { showSizeGuide = false }, colors = ButtonDefaults.buttonColors(containerColor = JoliBBPrimary)) {
                    Text("Fermer")
                }
            },
            title = { Text("Guide des Tailles Layette JoliBB", fontWeight = FontWeight.Bold, color = JoliBBPrimary) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("• Naissance : jusqu'à 50 cm / 3.3 kg", fontSize = 13.sp)
                    Text("• 0-1 mois : 50 - 54 cm / 4.0 kg", fontSize = 13.sp)
                    Text("• 3 mois : 54 - 60 cm / 5.5 kg", fontSize = 13.sp)
                    Text("• 6 mois : 60 - 67 cm / 7.5 kg", fontSize = 13.sp)
                    Text("• 12 mois : 67 - 74 cm / 10.0 kg", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Astuce : Si votre bébé pèse plus de 3.5 kg à la naissance, nous vous conseillons de commander directement du 1 mois !", fontSize = 12.sp, color = JoliBBPrimary, fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

@Composable
fun ProductMainDetails(
    product: Product,
    selectedSize: String,
    selectedColor: String,
    onSizeSelect: (String) -> Unit,
    onColorSelect: (String) -> Unit,
    onSizeGuideClick: () -> Unit,
    onAddToCart: () -> Unit,
    isAdding: Boolean
) {
    // Title
    Text(
        text = product.title,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 22.sp,
        color = TextDark
    )
    Spacer(modifier = Modifier.height(4.dp))

    // SKU & Rating Row
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(BorderColor)
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text("SKU: ${product.sku}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TextLight)
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFD54F), modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(2.dp))
            Text("${product.rating} (${product.reviewCount} avis)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextDark)
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Price Card
    Row(verticalAlignment = Alignment.CenterVertically) {
        if (product.isPromo) {
            Text(
                text = formatPrice(product.promoPriceXaf, product.promoPriceEur),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = SoftCoral
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = formatPrice(product.priceXaf, product.priceEur),
                fontSize = 14.sp,
                color = TextLight,
                textDecoration = TextDecoration.LineThrough
            )
            Spacer(modifier = Modifier.width(8.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(SoftCoral)
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("-${product.promoDiscountPercent}%", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        } else {
            Text(
                text = formatPrice(product.priceXaf, product.priceEur),
                fontWeight = FontWeight.ExtraBold,
                fontSize = 22.sp,
                color = TextDark
            )
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Description text
    Text("Description", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        text = product.descriptionLong,
        fontSize = 13.sp,
        lineHeight = 18.sp,
        color = TextDark
    )

    Spacer(modifier = Modifier.height(16.dp))

    // Attributes list (Matière, entretien)
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        border = BorderBorderStroke(),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row {
                Text("🌿 Matière : ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                Text(product.material, fontSize = 12.sp, color = TextLight)
            }
            Row {
                Text("🧼 Entretien : ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                Text(product.entretien, fontSize = 12.sp, color = TextLight)
            }
            Row {
                Text("📦 Stock : ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                Text(if (product.stock > 0) "Disponible (${product.stock} articles à Douala)" else "Rupture de stock", fontSize = 12.sp, color = if (product.stock > 0) SuccessGreen else SoftCoral, fontWeight = FontWeight.Bold)
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Option Selector 1: Sizes
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Choisir la Taille", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
        Text(
            text = "📐 Guide des tailles",
            fontSize = 12.sp,
            color = JoliBBPrimary,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { onSizeGuideClick() }
        )
    }
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        product.getSizeList().forEach { size ->
            val isSelected = selectedSize == size
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) JoliBBPrimary else BorderColor)
                    .clickable { onSizeSelect(size) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = size,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else TextDark
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Option Selector 2: Colors
    Text("Choisir la Couleur", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        product.getColorList().forEach { color ->
            val isSelected = selectedColor == color
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) JoliBBSecondary else BorderColor)
                    .clickable { onColorSelect(color) }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(24.dp))

    // Add to Cart Interactive Button
    Button(
        onClick = onAddToCart,
        enabled = product.stock > 0 && !isAdding,
        colors = ButtonDefaults.buttonColors(containerColor = SoftCoral, disabledContainerColor = Color.LightGray),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
    ) {
        if (isAdding) {
            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
        } else {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ajouter au panier • ${formatPrice(product.promoPriceXaf, product.promoPriceEur)}", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
        }
    }
}


// ==========================================
// 4. SHOPPING CART & MINI-CHECKOUT (PANIER RAPIDE)
// ==========================================
@Composable
fun CartScreen(
    viewModel: JoliBBViewModel,
    modifier: Modifier = Modifier
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val subtotalXaf by viewModel.cartSubtotalXaf.collectAsState()
    val subtotalEur by viewModel.cartSubtotalEur.collectAsState()
    val totalXaf by viewModel.cartTotalXaf.collectAsState()
    val totalEur by viewModel.cartTotalEur.collectAsState()
    val selectedDelivery by viewModel.selectedDelivery.collectAsState()

    val promoCode by viewModel.promoCode.collectAsState()
    val promoDiscountRatio by viewModel.promoDiscountRatio.collectAsState()
    val promoError by viewModel.promoError.collectAsState()

    var promoInput by remember { mutableStateOf("") }
    var addressInput by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(WarmCream)
    ) {
        // Upper back and currency row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = { viewModel.navigateBack() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = JoliBBPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Continuer les achats", color = JoliBBPrimary, fontWeight = FontWeight.Bold)
            }
            CurrencyToggle()
        }

        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                    Icon(Icons.Default.RemoveShoppingCart, contentDescription = "Panier vide", tint = JoliBBPrimary, modifier = Modifier.size(72.dp))
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Votre panier Layette JoliBB est vide.", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Découvrez nos indispensables layettes naissance en coton bio.", fontSize = 12.sp, color = TextLight, textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.navigateTo(Screen.Catalogue) },
                        colors = ButtonDefaults.buttonColors(containerColor = SoftCoral)
                    ) {
                        Text("Découvrir le catalogue", fontWeight = FontWeight.Bold)
                    }
                }
            }
            return
        }

        // Layout Split (Tablet-friendly side-by-side)
        val configuration = LocalConfiguration.current
        val isTablet = configuration.screenWidthDp >= 600

        if (isTablet) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Cart Items Column
                Column(modifier = Modifier.weight(1.3f)) {
                    Text("🛍️ Votre Panier (${cartItems.size} articles)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items(cartItems) { item ->
                            CartItemRow(item, viewModel)
                        }
                    }
                }

                // Checkout Column
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    CheckoutForm(
                        subtotalXaf, subtotalEur, totalXaf, totalEur,
                        selectedDelivery, viewModel, promoCode, promoDiscountRatio,
                        promoError, promoInput, addressInput,
                        onPromoChange = { promoInput = it },
                        onAddressChange = { addressInput = it },
                        onCheckout = {
                            launchWhatsAppCheckout(context, cartItems, selectedDelivery, totalXaf, totalEur, addressInput, promoCode, promoDiscountRatio)
                        }
                    )
                }
            }
        } else {
            // Mobile layout
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text("🛍️ Votre Panier (${cartItems.size} articles)", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TextDark)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                items(cartItems) { item ->
                    CartItemRow(item, viewModel)
                }

                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    CheckoutForm(
                        subtotalXaf, subtotalEur, totalXaf, totalEur,
                        selectedDelivery, viewModel, promoCode, promoDiscountRatio,
                        promoError, promoInput, addressInput,
                        onPromoChange = { promoInput = it },
                        onAddressChange = { addressInput = it },
                        onCheckout = {
                            launchWhatsAppCheckout(context, cartItems, selectedDelivery, totalXaf, totalEur, addressInput, promoCode, promoDiscountRatio)
                        }
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, viewModel: JoliBBViewModel) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        border = BorderBorderStroke(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mini Icon representer
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(PastelRose),
                contentAlignment = Alignment.Center
            ) {
                val (vector, tint) = getProductIconAndColor(item.title)
                Icon(imageVector = vector, contentDescription = item.title, tint = tint, modifier = Modifier.size(34.dp))
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Text info
            Column(modifier = Modifier.weight(1f)) {
                Text(item.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("Taille : ${item.selectedSize} | Couleur : ${item.selectedColor}", fontSize = 11.sp, color = TextLight)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatPrice(item.priceXaf, item.priceEur),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = JoliBBPrimary
                )
            }

            // Controls
            Column(horizontalAlignment = Alignment.End) {
                IconButton(onClick = { viewModel.removeCartItem(item) }, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = SoftCoral, modifier = Modifier.size(16.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BorderColor)
                            .clickable { viewModel.updateCartItemQuantity(item, -1) }
                            .size(22.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                    }

                    Text(item.quantity.toString(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextDark)

                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(BorderColor)
                            .clickable { viewModel.updateCartItemQuantity(item, 1) }
                            .size(22.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                    }
                }
            }
        }
    }
}

@Composable
fun CheckoutForm(
    subtotalXaf: Double,
    subtotalEur: Double,
    totalXaf: Double,
    totalEur: Double,
    selectedDelivery: Neighborhood,
    viewModel: JoliBBViewModel,
    promoCode: String,
    promoDiscountRatio: Double,
    promoError: String?,
    promoInput: String,
    addressInput: String,
    onPromoChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onCheckout: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth(),
        border = BorderBorderStroke(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("📍 Options de Livraison Douala", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))

            // Neighborhood selector dropdown simulator
            var expandedDropdown by remember { mutableStateOf(false) }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(WarmCream)
                    .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                    .clickable { expandedDropdown = !expandedDropdown }
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalShipping, contentDescription = "Delivery", tint = JoliBBPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(selectedDelivery.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = TextDark)
                        Text("${selectedDelivery.deliveryInfo} • ${formatPrice(selectedDelivery.feeXaf, selectedDelivery.feeXaf / 655.95)}", fontSize = 11.sp, color = TextLight)
                    }
                    Icon(if (expandedDropdown) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown, contentDescription = "Drop")
                }
            }

            if (expandedDropdown) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    border = BorderBorderStroke()
                ) {
                    Column(modifier = Modifier.height(200.dp).verticalScroll(rememberScrollState())) {
                        viewModel.doualaNeighborhoods.forEach { neighborhood ->
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.selectDelivery(neighborhood)
                                        expandedDropdown = false
                                    }
                                    .padding(12.dp)
                            ) {
                                Text(neighborhood.name, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
                                Text("${neighborhood.deliveryInfo} • ${formatPrice(neighborhood.feeXaf, neighborhood.feeXaf / 655.95)}", fontSize = 10.sp, color = TextLight)
                            }
                            HorizontalDivider(color = BorderColor)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Delivery address local inputs
            Text("Quartier & Adresse exacte (Douala)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = addressInput,
                onValueChange = onAddressChange,
                placeholder = { Text("Ex: Rue des Palmiers, à côté de la pharmacie, Bonamoussadi...", fontSize = 11.sp) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JoliBBPrimary, unfocusedBorderColor = BorderColor)
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(16.dp))

            // Promo Code Section
            Text("🎁 Code Promo (JOLIBB10, BIENVENUE20)", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = TextDark)
            Spacer(modifier = Modifier.height(6.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = promoInput,
                    onValueChange = onPromoChange,
                    placeholder = { Text("Entrez le code promo", fontSize = 11.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = JoliBBPrimary, unfocusedBorderColor = BorderColor),
                    modifier = Modifier.weight(1f).height(50.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = { viewModel.applyPromoCode(promoInput) },
                    colors = ButtonDefaults.buttonColors(containerColor = JoliBBPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(50.dp)
                ) {
                    Text("Appliquer", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (promoCode.isNotEmpty()) {
                Text("Code ${promoCode} appliqué : -${(promoDiscountRatio * 100).toInt()}% de réduction !", color = SuccessGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            }
            if (promoError != null) {
                Text(promoError, color = SoftCoral, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(16.dp))

            // Subtotal and Calculations
            val discountXaf = subtotalXaf * promoDiscountRatio
            val finalSubtotalXaf = subtotalXaf - discountXaf
            val finalTotalXaf = finalSubtotalXaf + selectedDelivery.feeXaf

            val discountEur = subtotalEur * promoDiscountRatio
            val finalSubtotalEur = subtotalEur - discountEur
            val finalTotalEur = finalSubtotalEur + (selectedDelivery.feeXaf / 655.95)

            Text("🧾 RÉSUMÉ DE LA COMMANDE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextDark)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Sous-total articles :", fontSize = 13.sp, color = TextLight)
                Text(formatPrice(subtotalXaf, subtotalEur), fontSize = 13.sp, color = TextDark)
            }
            if (promoDiscountRatio > 0.0) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Réduction (${promoCode}) :", fontSize = 13.sp, color = SuccessGreen)
                    Text("-${formatPrice(discountXaf, discountEur)}", fontSize = 13.sp, color = SuccessGreen)
                }
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Frais de livraison (${selectedDelivery.name}) :", fontSize = 13.sp, color = TextLight)
                Text(formatPrice(selectedDelivery.feeXaf, selectedDelivery.feeXaf / 655.95), fontSize = 13.sp, color = TextDark)
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = BorderColor)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("TOTAL NET À PAYER :", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = TextDark)
                Text(formatPrice(finalTotalXaf, finalTotalEur), fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = JoliBBPrimary)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // WhatsApp Order submit CTA button
            Button(
                onClick = onCheckout,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)), // WhatsApp green
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Phone, contentDescription = "WhatsApp", tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Finaliser sur WhatsApp (Paiement Livraison)", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "💡 Le clic ouvrira WhatsApp avec le récapitulatif prérempli de votre panier pour finaliser avec Layette JoliBB.",
                fontSize = 10.sp,
                color = TextLight,
                textAlign = TextAlign.Center,
                lineHeight = 12.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

/**
 * Utility function to generate a pre-filled WhatsApp checkout link based on cart contents and order parameters.
 */
fun generateWhatsAppLink(
    items: List<CartItem>,
    delivery: Neighborhood,
    totalXaf: Double,
    totalEur: Double,
    address: String,
    promoCode: String,
    promoDiscount: Double,
    whatsAppNumber: String = "237693609037"
): String {
    val sb = StringBuilder()
    sb.append("🛒 *NOUVELLE COMMANDE - LAYETTE JOLIBB*\n\n")
    sb.append("Bonjour JoliBB, je souhaite finaliser ma commande d'articles de naissance :\n\n")

    items.forEachIndexed { index, item ->
        sb.append("${index + 1}) *${item.title}*\n")
        sb.append("   • Quantité : ${item.quantity}\n")
        sb.append("   • Taille : ${item.selectedSize}\n")
        sb.append("   • Couleur : ${item.selectedColor}\n")
        sb.append("   • Prix : ${String.format("%,.0f FCFA", item.priceXaf)} (${String.format("%.2f €", item.priceEur)})\n\n")
    }

    if (promoCode.isNotEmpty()) {
        sb.append("🎁 *Code Promo utilisé :* $promoCode (-${(promoDiscount * 100).toInt()}%)\n")
    }

    sb.append("📍 *Mode de livraison :* ${delivery.name}\n")
    sb.append("🏠 *Adresse Douala :* ${if (address.isEmpty()) "Non précisé" else address}\n\n")

    sb.append("💰 *TOTAL COMMANDE :*\n")
    sb.append("👉 *${String.format("%,.0f FCFA", totalXaf)}*\n")
    sb.append("👉 *${String.format("%.2f €", totalEur)}*\n\n")

    sb.append("Paiement prévu à la livraison. Merci de valider la disponibilité !")

    val encodedMsg = URLEncoder.encode(sb.toString(), "UTF-8")
    return "https://wa.me/$whatsAppNumber?text=$encodedMsg"
}

// Prefills WhatsApp message according to the requirements using the utility generator function
fun launchWhatsAppCheckout(
    context: android.content.Context,
    items: List<CartItem>,
    delivery: Neighborhood,
    totalXaf: Double,
    totalEur: Double,
    address: String,
    promoCode: String,
    promoDiscount: Double
) {
    try {
        val url = generateWhatsAppLink(
            items = items,
            delivery = delivery,
            totalXaf = totalXaf,
            totalEur = totalEur,
            address = address,
            promoCode = promoCode,
            promoDiscount = promoDiscount,
            whatsAppNumber = "237693609037"
        )
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        context.startActivity(intent)
    } catch (e: Exception) {
        android.widget.Toast.makeText(context, "Erreur lors de la redirection WhatsApp", android.widget.Toast.LENGTH_SHORT).show()
    }
}
