package lol.dutchu.myweather.data.mappers

import lol.dutchu.myweather.data.remote.WeatherDataDto
import lol.dutchu.myweather.data.remote.WeatherDto
import lol.dutchu.myweather.domain.weather.WeatherData
import lol.dutchu.myweather.domain.weather.WeatherInfo
import lol.dutchu.myweather.domain.weather.WeatherType
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private data class IndexedWeatherData(
    val index: Int,
    val data: WeatherData
)

fun WeatherDataDto.toWeatherDataMap(): Map<Int, List<WeatherData>> {
    return time.mapIndexed { index, time ->
        val temperature = temperatures[index]
        val pressure = pressures[index]
        val windSpeed = windSpeeds[index]
        val humidity = humidities[index]
        val weatherCode = weatherCodes[index]
        IndexedWeatherData(
            index,
            WeatherData(
                LocalDateTime.parse(time, DateTimeFormatter.ISO_DATE_TIME),
                temperature,
                pressure,
                windSpeed,
                humidity,
                WeatherType.fromWMO(weatherCode)
            )
        )
    }.groupBy {
        it.index / 24
    }.mapValues {
        it.value.map { it.data }
    }
}

fun WeatherDto.toWeatherInfo(): WeatherInfo {
    val weatherDataMap = weatherData.toWeatherDataMap()
    val now = LocalDateTime.now()
    val currentWeatherData = weatherDataMap[0]?.find {
        val hour = if(now.minute < 30) now.hour else now.hour + 1
        it.time.hour == hour
    }
    return WeatherInfo(
        weatherDataMap,
        currentWeatherData
    )
}