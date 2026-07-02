package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import com.example.R
import com.example.ui.FilterState
import com.example.ui.JoliBBViewModel
import com.example.ui.Screen
import com.example.ui.theme.*

@Composable
fun JoliBBHeader(
    viewModel: JoliBBViewModel,
    modifier: Modifier = Modifier
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val filterState by viewModel.filters.collectAsState()
    val suggestions by viewModel.searchSuggestions.collectAsState()
    val totalCartQty = cartItems.sumOf { it.quantity }
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchFocused by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        tonalElevation = 4.dp,
        color = Color.White
    ) {
        Column {
            // Local Announcement Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(JoliBBPrimary)
                    .padding(vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "✨ Boutique Douala Akwa • Commande WhatsApp • Paiement Livraison locale XAF",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            // Main Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Client Logo Area
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.Accueil) }
                        .padding(end = 8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_logo),
                        contentDescription = "Logo Layette JoliBB",
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .border(1.dp, JoliBBPrimary, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "JoliBB",
                            color = JoliBBPrimary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            lineHeight = 22.sp
                        )
                        Text(
                            text = "Layette Naissance",
                            color = TextLight,
                            fontWeight = FontWeight.Normal,
                            fontSize = 10.sp
                        )
                    }
                }

                // Search Bar with Suggestion Dropdown
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp)
                ) {
                    OutlinedTextField(
                        value = filterState.searchQuery,
                        onValueChange = { 
                            viewModel.updateSearchQuery(it)
                            searchFocused = it.isNotEmpty()
                        },
                        placeholder = { Text("Rechercher body, pyjama...", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = JoliBBPrimary) },
                        trailingIcon = {
                            if (filterState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = JoliBBPrimary,
                            unfocusedBorderColor = BorderColor,
                            focusedContainerColor = WarmCream,
                            unfocusedContainerColor = WarmCream
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            keyboardController?.hide()
                            searchFocused = false
                            viewModel.navigateTo(Screen.Catalogue)
                        }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    )

                    // Autocomplete suggestion popup
                    if (searchFocused && suggestions.isNotEmpty()) {
                        Popup(
                            alignment = Alignment.BottomStart,
                            onDismissRequest = { searchFocused = false }
                        ) {
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                                modifier = Modifier
                                    .padding(top = 54.dp)
                                    .fillMaxWidth(0.9f)
                                    .background(Color.White)
                                    .border(1.dp, BorderColor, RoundedCornerShape(8.dp))
                            ) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    suggestions.forEach { suggestion ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clickable {
                                                    viewModel.updateSearchQuery(suggestion)
                                                    searchFocused = false
                                                    keyboardController?.hide()
                                                    viewModel.navigateTo(Screen.Catalogue)
                                                }
                                                .padding(vertical = 10.dp, horizontal = 12.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(Icons.Default.History, contentDescription = "Sugg", tint = TextLight, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Text(suggestion, fontSize = 14.sp, color = TextDark)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Interactive Icons (Admin Account & Fast Cart)
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Profile Icon -> Admin
                    IconButton(onClick = { viewModel.navigateTo(Screen.AdminLogin) }) {
                        Icon(
                            imageVector = Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Espace Admin",
                            tint = if (currentScreen is Screen.AdminDashboard || currentScreen is Screen.AdminLogin) JoliBBPrimary else TextDark
                        )
                    }

                    // Cart Icon with dynamic badge
                    Box(
                        modifier = Modifier.clickable { viewModel.navigateTo(Screen.Panier) }
                    ) {
                        IconButton(onClick = { viewModel.navigateTo(Screen.Panier) }) {
                            Icon(
                                imageVector = Icons.Outlined.ShoppingCart,
                                contentDescription = "Panier rapide",
                                tint = if (currentScreen is Screen.Panier) JoliBBPrimary else TextDark
                            )
                        }
                        if (totalCartQty > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .offset(x = (-4).dp, y = 4.dp)
                                    .size(18.dp)
                                    .background(SoftCoral, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = totalCartQty.toString(),
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            // Horizontal Categories Tab Navigation Menu
            ScrollableTabRow(
                selectedTabIndex = getSelectedTabIndex(currentScreen, filterState.category),
                edgePadding = 16.dp,
                containerColor = Color.White,
                contentColor = JoliBBPrimary,
                indicator = { tabPositions ->
                    if (getSelectedTabIndex(currentScreen, filterState.category) in tabPositions.indices) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[getSelectedTabIndex(currentScreen, filterState.category)]),
                            color = JoliBBPrimary
                        )
                    }
                }
            ) {
                // Home tab
                Tab(
                    selected = currentScreen is Screen.Accueil,
                    onClick = {
                        viewModel.resetFilters()
                        viewModel.navigateTo(Screen.Accueil)
                    },
                    text = { Text("Accueil", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )

                // Layout category 1: Layette Naissance
                Tab(
                    selected = currentScreen is Screen.Catalogue && filterState.category == "Layette Naissance",
                    onClick = {
                        viewModel.resetFilters()
                        viewModel.setCategoryFilter("Layette Naissance")
                        viewModel.navigateTo(Screen.Catalogue)
                    },
                    text = { Text("Layette Naissance", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )

                // Layout category 2: Vêtements 0–3 mois
                Tab(
                    selected = currentScreen is Screen.Catalogue && filterState.category == "Vêtements 0-3 mois",
                    onClick = {
                        viewModel.resetFilters()
                        viewModel.setCategoryFilter("Vêtements 0-3 mois")
                        viewModel.navigateTo(Screen.Catalogue)
                    },
                    text = { Text("Vêtements 0–3 mois", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )

                // Layout category 3: Accessoires
                Tab(
                    selected = currentScreen is Screen.Catalogue && filterState.category == "Accessoires",
                    onClick = {
                        viewModel.resetFilters()
                        viewModel.setCategoryFilter("Accessoires")
                        viewModel.navigateTo(Screen.Catalogue)
                    },
                    text = { Text("Accessoires", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )

                // Layout category 4: Cadeaux naissance
                Tab(
                    selected = currentScreen is Screen.Catalogue && filterState.category == "Cadeaux naissance",
                    onClick = {
                        viewModel.resetFilters()
                        viewModel.setCategoryFilter("Cadeaux naissance")
                        viewModel.navigateTo(Screen.Catalogue)
                    },
                    text = { Text("Cadeaux Naissance", fontSize = 13.sp, fontWeight = FontWeight.Bold) }
                )
            }
        }
    }
}

private fun getSelectedTabIndex(screen: Screen, category: String): Int {
    if (screen is Screen.Accueil) return 0
    if (screen is Screen.Catalogue) {
        return when (category) {
            "Layette Naissance" -> 1
            "Vêtements 0-3 mois", "Vêtements 0–3 mois" -> 2
            "Accessoires" -> 3
            "Cadeaux naissance", "Cadeaux Naissance" -> 4
            else -> 0
        }
    }
    return 0
}

@Composable
fun JoliBBFooter(
    viewModel: JoliBBViewModel,
    modifier: Modifier = Modifier
) {
    var emailInput by remember { mutableStateOf("") }
    var newsSigned by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(TextDark)
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        // Brand & Description
        Text(
            text = "Layette JoliBB • Douala",
            color = JoliBBPrimary,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Inspiré de l'élégance layette française, confectionné avec soin pour la douceur et le bien-être de vos bébés au Cameroun. Vente de vêtements et accessoires maternité de 0 à 12 mois.",
            color = Color.LightGray,
            fontSize = 12.sp,
            lineHeight = 16.sp
        )

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Color.DarkGray)
        Spacer(modifier = Modifier.height(20.dp))

        // Grid contents: Contact & Hours
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Contacts Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "📍 CONTACTEZ-NOUS",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Boutique JoliBB, Rue Joss (Près Clinique de la Maternité), Akwa, Douala, Cameroun.",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "📞 Tél / WhatsApp : +237 6 93 60 90 37",
                    color = JoliBBSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "✉️ Email : contact@jolibb.cm",
                    color = Color.LightGray,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Opening Hours Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "⏰ HORAIRES D'OUVERTURE",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Lundi - Vendredi : 08h30 - 19h00\nSamedi : 09h00 - 18h00\nDimanche : Fermé (Retraits sur RDV)",
                    color = Color.LightGray,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
        HorizontalDivider(color = Color.DarkGray)
        Spacer(modifier = Modifier.height(20.dp))

        // Utilitary Links & Policies
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "ℹ️ INFOS UTILES",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• Livraison locale Douala (1-2 jours)\n• Retours autorisés sous 14 jours\n• Garantie douceur 100% coton bio\n• Guide des Tailles Layette",
                    color = Color.LightGray,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }

            // Newsletter
            Column(modifier = Modifier.weight(1.2f)) {
                Text(
                    text = "💌 NEWSLETTER JOLIBB",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                if (!newsSigned) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = emailInput,
                            onValueChange = { emailInput = it },
                            placeholder = { Text("Votre email...", fontSize = 11.sp) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                focusedTextColor = TextDark,
                                unfocusedTextColor = TextDark
                            ),
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                        )
                        Button(
                            onClick = {
                                if (emailInput.isNotEmpty()) newsSigned = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = JoliBBPrimary),
                            shape = RoundedCornerShape(topEnd = 4.dp, bottomEnd = 4.dp),
                            modifier = Modifier.height(46.dp)
                        ) {
                            Text("OK", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Text(
                        text = "Merci pour votre inscription ! 🎉",
                        color = SuccessGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider(color = Color.DarkGray)
        Spacer(modifier = Modifier.height(16.dp))

        // Copyright and Socials
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "© 2026 Layette JoliBB. Tous droits réservés.\nInspiré par Vertbaudet. Réalisé pour Douala.",
                color = Color.Gray,
                fontSize = 10.sp,
                lineHeight = 14.sp,
                modifier = Modifier.weight(1f)
            )

            // Social Buttons Row
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = {}, modifier = Modifier.size(32.dp).background(Color.DarkGray, CircleShape)) {
                    Icon(Icons.Default.Phone, contentDescription = "WhatsApp", tint = Color.White, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = {}, modifier = Modifier.size(32.dp).background(Color.DarkGray, CircleShape)) {
                    Icon(Icons.Default.LocationOn, contentDescription = "Maps", tint = Color.White, modifier = Modifier.size(16.dp))
                }
                IconButton(onClick = {}, modifier = Modifier.size(32.dp).background(Color.DarkGray, CircleShape)) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}
