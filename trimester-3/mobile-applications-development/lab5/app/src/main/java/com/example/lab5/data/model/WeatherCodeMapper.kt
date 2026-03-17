package com.example.lab5.data.model

/**
 * Maps WMO Weather interpretation codes to human-readable descriptions
 * and OpenWeatherMap icon URLs (publicly accessible, no key needed).
 */
object WeatherCodeMapper {

    data class WeatherMapping(
        val description: String,
        val owmIconDay: String,
        val owmIconNight: String,
        val backgroundUrl: String
    )

    private val codeMap = mapOf(
        0 to WeatherMapping("Clear sky", "01d", "01n", "https://images.unsplash.com/photo-1622396481328-9bceb6fdc29b?auto=format&fit=crop&w=1080&q=80"), // clear blue sky
        1 to WeatherMapping("Mainly clear", "02d", "02n", "https://images.unsplash.com/photo-1549277513-f1b32fe1f8c5?auto=format&fit=crop&w=1080&q=80"), // mostly clear
        2 to WeatherMapping("Partly cloudy", "03d", "03n", "https://images.unsplash.com/photo-1595152772590-b9835f11100f?auto=format&fit=crop&w=1080&q=80"), // partly cloudy
        3 to WeatherMapping("Overcast", "04d", "04n", "https://images.unsplash.com/photo-1534088568595-a066f410cbda?auto=format&fit=crop&w=1080&q=80"), // overcast/clouds
        45 to WeatherMapping("Foggy", "50d", "50n", "https://images.unsplash.com/photo-1517482813137-7fef482be4de?auto=format&fit=crop&w=1080&q=80"), // fog
        48 to WeatherMapping("Depositing rime fog", "50d", "50n", "https://images.unsplash.com/photo-1487621167305-5d248087c724?auto=format&fit=crop&w=1080&q=80"), // freezing fog
        51 to WeatherMapping("Light drizzle", "09d", "09n", "https://images.unsplash.com/photo-1541696432-82c6da8ce7bf?auto=format&fit=crop&w=1080&q=80"), // drizzle
        53 to WeatherMapping("Moderate drizzle", "09d", "09n", "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?auto=format&fit=crop&w=1080&q=80"), // drizzle
        55 to WeatherMapping("Dense drizzle", "09d", "09n", "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?auto=format&fit=crop&w=1080&q=80"),
        56 to WeatherMapping("Light freezing drizzle", "09d", "09n", "https://images.unsplash.com/photo-1476611417539-df2ceb33dc72?auto=format&fit=crop&w=1080&q=80"), // ice/freezing rain
        57 to WeatherMapping("Dense freezing drizzle", "09d", "09n", "https://images.unsplash.com/photo-1476611417539-df2ceb33dc72?auto=format&fit=crop&w=1080&q=80"),
        61 to WeatherMapping("Slight rain", "10d", "10n", "https://images.unsplash.com/photo-1519692933481-e162a57d6721?auto=format&fit=crop&w=1080&q=80"), // light rain
        63 to WeatherMapping("Moderate rain", "10d", "10n", "https://images.unsplash.com/photo-1428592953211-077101b2021b?auto=format&fit=crop&w=1080&q=80"), // rain window
        65 to WeatherMapping("Heavy rain", "10d", "10n", "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?auto=format&fit=crop&w=1080&q=80"), // heavy rain
        66 to WeatherMapping("Light freezing rain", "13d", "13n", "https://images.unsplash.com/photo-1610484501438-23212002f23b?auto=format&fit=crop&w=1080&q=80"), // frozen trees
        67 to WeatherMapping("Heavy freezing rain", "13d", "13n", "https://images.unsplash.com/photo-1542159811-0ce70f90e909?auto=format&fit=crop&w=1080&q=80"),
        71 to WeatherMapping("Slight snowfall", "13d", "13n", "https://images.unsplash.com/photo-1505322022379-7c3353ee6291?auto=format&fit=crop&w=1080&q=80"), // snow town
        73 to WeatherMapping("Moderate snowfall", "13d", "13n", "https://images.unsplash.com/photo-1514782831304-632d8450625f?auto=format&fit=crop&w=1080&q=80"), // snow covered
        75 to WeatherMapping("Heavy snowfall", "13d", "13n", "https://images.unsplash.com/photo-1547432585-802c63602183?auto=format&fit=crop&w=1080&q=80"), // heavy snow
        77 to WeatherMapping("Snow grains", "13d", "13n", "https://images.unsplash.com/photo-1505322022379-7c3353ee6291?auto=format&fit=crop&w=1080&q=80"),
        80 to WeatherMapping("Slight rain showers", "09d", "09n", "https://images.unsplash.com/photo-1536100171097-df081f9bdef8?auto=format&fit=crop&w=1080&q=80"), // shower
        81 to WeatherMapping("Moderate rain showers", "09d", "09n", "https://images.unsplash.com/photo-1520609798519-2e1e8c18df3a?auto=format&fit=crop&w=1080&q=80"),
        82 to WeatherMapping("Violent rain showers", "09d", "09n", "https://images.unsplash.com/photo-1515694346937-94d85e41e6f0?auto=format&fit=crop&w=1080&q=80"),
        85 to WeatherMapping("Slight snow showers", "13d", "13n", "https://images.unsplash.com/photo-1483664852095-d6cc6870702d?auto=format&fit=crop&w=1080&q=80"), // snow falling
        86 to WeatherMapping("Heavy snow showers", "13d", "13n", "https://images.unsplash.com/photo-1514241516423-6447847cceea?auto=format&fit=crop&w=1080&q=80"),
        95 to WeatherMapping("Thunderstorm", "11d", "11n", "https://images.unsplash.com/photo-1605727216801-e27ce1d0ce3c?auto=format&fit=crop&w=1080&q=80"), // lightning
        96 to WeatherMapping("Thunderstorm with slight hail", "11d", "11n", "https://images.unsplash.com/photo-1551582045-6cb9c16caddc?auto=format&fit=crop&w=1080&q=80"), // storm
        99 to WeatherMapping("Thunderstorm with heavy hail", "11d", "11n", "https://images.unsplash.com/photo-1429552077091-836152271555?auto=format&fit=crop&w=1080&q=80") // huge storm
    )

    fun getDescription(code: Int): String {
        return codeMap[code]?.description ?: "Unknown"
    }

    fun getIconUrl(code: Int, isDay: Boolean = true, large: Boolean = true): String {
        val mapping = codeMap[code]
        val iconCode = if (isDay) mapping?.owmIconDay ?: "01d" else mapping?.owmIconNight ?: "01n"
        val size = if (large) "4x" else "2x"
        return "https://openweathermap.org/img/wn/${iconCode}@${size}.png"
    }

    fun getWeatherInfo(code: Int, isDay: Boolean = true): WeatherInfo {
        val backgroundUrl = codeMap[code]?.backgroundUrl ?: "https://images.unsplash.com/photo-1534088568595-a066f410cbda?auto=format&fit=crop&w=1080&q=80"
        return WeatherInfo(
            description = getDescription(code),
            iconUrl = getIconUrl(code, isDay, large = true),
            smallIconUrl = getIconUrl(code, isDay, large = false),
            largeImageUrl = backgroundUrl
        )
    }
}
