package lol.dutchu.myweather.presentation.weather

import lol.dutchu.myweather.domain.location.LocationData
import lol.dutchu.myweather.domain.weather.WeatherInfo

data class WeatherState(
    val weatherInfo: WeatherInfo? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = "",
    val searchResults: List<LocationData> = emptyList(),
    val isSearching: Boolean = false,
    val selectedDayIndex: Int = 0,
    val useGpsLocation: Boolean = true // Added this flag
)