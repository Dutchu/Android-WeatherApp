package lol.dutchu.myweather.presentation.location

import lol.dutchu.myweather.domain.weather.WeatherInfo

data class LocationState(
    val weatherInfo: WeatherInfo? = null,
    val locationName: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)