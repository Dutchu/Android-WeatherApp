package lol.dutchu.myweather.data.repository

import lol.dutchu.myweather.data.mappers.toWeatherInfo
import lol.dutchu.myweather.data.remote.WeatherApi
import lol.dutchu.myweather.domain.repository.WeatherRepository
import lol.dutchu.myweather.domain.util.Resource
import lol.dutchu.myweather.domain.weather.WeatherInfo
import javax.inject.Inject


class WeatherRepositoryImpl @Inject constructor(
    private val api: WeatherApi
) : WeatherRepository {
    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Resource<WeatherInfo> {
        return try {
            Resource.Success(
                api.getWeatherInfo(
                    latitude,
                    longitude
                ).toWeatherInfo()
            )
        } catch(e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occured.")
        }
    }
}