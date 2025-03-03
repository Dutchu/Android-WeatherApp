package lol.dutchu.myweather.domain.repository

import lol.dutchu.myweather.domain.util.Resource
import lol.dutchu.myweather.domain.weather.WeatherInfo

interface WeatherRepository {
    suspend fun getWeatherInfo(latitude: Double, longitude: Double): Resource<WeatherInfo>
}