package lol.dutchu.myweather.domain.weather

import lol.dutchu.myweather.data.remote.WeatherDataDto

data class WeatherInfo (
    val weatherDataPerDay: Map<Int, List<WeatherData>>,
    val currentWeatherData: WeatherData?
)