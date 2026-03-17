package com.example.lab5.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Air
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.Compress
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.NightsStay
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.lab5.data.model.WeatherCodeMapper
import com.example.lab5.ui.theme.PinkGradientEnd
import com.example.lab5.ui.theme.PinkGradientStart
import com.example.lab5.ui.theme.PinkPrimary
import com.example.lab5.ui.viewmodel.WeatherViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WeatherDetailScreen(
    viewModel: WeatherViewModel,
    onBackClick: () -> Unit
) {
    val state by viewModel.detailState.collectAsState()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(color = PinkPrimary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading weather...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            state.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ErrorOutline,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = PinkPrimary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.error!!,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onBackClick,
                            colors = ButtonDefaults.buttonColors(containerColor = PinkPrimary)
                        ) {
                            Text("Go Back")
                        }
                    }
                }
            }

            state.weather?.current != null -> {
                val weather = state.weather!!
                val current = weather.current!!
                val isDay = (current.isDay ?: 1) == 1
                val weatherInfo = WeatherCodeMapper.getWeatherInfo(current.weatherCode, isDay)
                val daily = weather.daily

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState)
                ) {
                    // Top section with background image and weather info
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp) // Taller header to show off the image
                    ) {
                        // Background Image
                        AsyncImage(
                            model = weatherInfo.largeImageUrl,
                            contentDescription = "Weather background",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                        
                        // Dark overlay for text readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.4f))
                        )
                        
                        // Gradient fading to background color at the bottom
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Transparent,
                                            MaterialTheme.colorScheme.background
                                        )
                                    )
                                )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .statusBarsPadding()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Back button and city name
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                IconButton(
                                    onClick = onBackClick,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.3f))
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = state.cityName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1
                                )
                            }

                            Spacer(modifier = Modifier.weight(1f))

                            // Large weather icon
                            AsyncImage(
                                model = weatherInfo.iconUrl,
                                contentDescription = weatherInfo.description,
                                modifier = Modifier.size(160.dp),
                                contentScale = ContentScale.Fit
                            )

                            // Temperature
                            Text(
                                text = "${current.temperature.toInt()}°C",
                                fontSize = 64.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            // Description
                            Text(
                                text = weatherInfo.description,
                                style = MaterialTheme.typography.titleMedium,
                                color = Color.White.copy(alpha = 0.9f),
                                fontWeight = FontWeight.Medium
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Feels like + High/Low
                            val feelsLike = current.apparentTemperature.toInt()
                            val highLow = if (daily != null && daily.tempMax.isNotEmpty()) {
                                "H: ${daily.tempMax[0].toInt()}°  L: ${daily.tempMin[0].toInt()}°"
                            } else ""

                            Text(
                                text = "Feels like ${feelsLike}°C  •  $highLow",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )

                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Weather details grid
                    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                        Text(
                            text = "Weather Details",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            WeatherDetailCard(
                                icon = Icons.Default.WaterDrop,
                                label = "Humidity",
                                value = "${current.humidity}%",
                                modifier = Modifier.weight(1f)
                            )
                            WeatherDetailCard(
                                icon = Icons.Default.Air,
                                label = "Wind",
                                value = "${current.windSpeed} km/h",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            WeatherDetailCard(
                                icon = Icons.Default.Compress,
                                label = "Pressure",
                                value = "${current.pressure.toInt()} hPa",
                                modifier = Modifier.weight(1f)
                            )
                            WeatherDetailCard(
                                icon = Icons.Default.Cloud,
                                label = "Cloud Cover",
                                value = "${current.cloudCover}%",
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Sunrise / Sunset (from daily data)
                        if (daily != null && daily.sunrise != null && daily.sunset != null &&
                            daily.sunrise.isNotEmpty() && daily.sunset.isNotEmpty()) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                WeatherDetailCard(
                                    icon = Icons.Default.WbSunny,
                                    label = "Sunrise",
                                    value = formatIsoTime(daily.sunrise[0]),
                                    modifier = Modifier.weight(1f)
                                )
                                WeatherDetailCard(
                                    icon = Icons.Default.NightsStay,
                                    label = "Sunset",
                                    value = formatIsoTime(daily.sunset[0]),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Forecast section
                    if (daily != null && daily.time.size > 1) {
                        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                            Text(
                                text = "7-Day Forecast",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                            )

                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                itemsIndexed(daily.time) { index, dateStr ->
                                    if (index > 0) { // Skip today (already shown above)
                                        ForecastCard(
                                            dateString = dateStr,
                                            weatherCode = daily.weatherCode[index],
                                            tempMax = daily.tempMax[index],
                                            tempMin = daily.tempMin[index]
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

@Composable
fun WeatherDetailCard(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = PinkPrimary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun ForecastCard(
    dateString: String,
    weatherCode: Int,
    tempMax: Double,
    tempMin: Double
) {
    val weatherInfo = WeatherCodeMapper.getWeatherInfo(weatherCode, isDay = true)

    // Parse date for display
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())
    val dateDisplayFormat = SimpleDateFormat("dd MMM", Locale.getDefault())

    val date = try {
        inputFormat.parse(dateString)
    } catch (e: Exception) {
        null
    }

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .width(100.dp)
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (date != null) dayFormat.format(date) else dateString,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = PinkPrimary
            )
            Text(
                text = if (date != null) dateDisplayFormat.format(date) else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            AsyncImage(
                model = weatherInfo.smallIconUrl,
                contentDescription = weatherInfo.description,
                modifier = Modifier.size(56.dp),
                contentScale = ContentScale.Fit
            )

            Text(
                text = "${tempMax.toInt()}° / ${tempMin.toInt()}°",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = weatherInfo.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                lineHeight = 14.sp
            )
        }
    }
}

private fun formatIsoTime(isoString: String): String {
    return try {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.getDefault())
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
        val date = inputFormat.parse(isoString)
        if (date != null) outputFormat.format(date) else isoString
    } catch (e: Exception) {
        isoString
    }
}
