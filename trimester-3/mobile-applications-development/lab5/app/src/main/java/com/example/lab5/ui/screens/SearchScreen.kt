package com.example.lab5.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.lab5.data.model.GeocodingResult
import com.example.lab5.ui.theme.PinkGradientEnd
import com.example.lab5.ui.theme.PinkGradientStart
import com.example.lab5.ui.theme.PinkPrimary
import com.example.lab5.ui.theme.PinkPrimaryLight
import com.example.lab5.ui.viewmodel.WeatherViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: WeatherViewModel,
    onCitySelected: (Long) -> Unit
) {
    val state by viewModel.searchState.collectAsState()
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Header with gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(PinkGradientStart, PinkGradientEnd)
                    )
                )
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 24.dp)
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(bottom = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Cloud,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Weather",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                // Search bar
                OutlinedTextField(
                    value = state.query,
                    onValueChange = { viewModel.updateQuery(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            "Search for a city...",
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White
                        )
                    },
                    trailingIcon = {
                        if (state.query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.clearSearch() }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        focusedBorderColor = Color.White.copy(alpha = 0.8f),
                        unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                        focusedContainerColor = Color.White.copy(alpha = 0.15f),
                        unfocusedContainerColor = Color.White.copy(alpha = 0.1f),
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            viewModel.searchCity()
                            focusManager.clearFocus()
                        }
                    )
                )
            }
        }

        // Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when {
                state.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = PinkPrimary
                    )
                }

                state.error != null -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Cloud,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = PinkPrimaryLight.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.error!!,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }

                state.results.isEmpty() && state.query.isEmpty() -> {
                    // Empty state
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = PinkPrimaryLight.copy(alpha = 0.4f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Search for a City",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Type a city name and press search",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                text = "${state.results.size} Result${if (state.results.size != 1) "s" else ""}",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                            )
                        }
                        items(state.results) { city ->
                            CityResultCard(
                                city = city,
                                onClick = {
                                    onCitySelected(city.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CityResultCard(
    city: GeocodingResult,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Location icon in circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(PinkGradientStart, PinkGradientEnd)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = city.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = buildString {
                        city.admin1?.let { append("$it, ") }
                        city.country?.let { append(it) }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "View Weather",
                tint = PinkPrimary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
