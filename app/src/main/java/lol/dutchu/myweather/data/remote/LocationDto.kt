package lol.dutchu.myweather.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class OpenWeatherDto(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@JsonClass(generateAdapter = true)
data class CloudsDto(
    val all: Int
)

@JsonClass(generateAdapter = true)
data class SysDto(
    val country: String?
)

@JsonClass(generateAdapter = true)
data class WindDto(
    val speed: Double,
    val deg: Int
)

@JsonClass(generateAdapter = true)
data class CoordDto(
    val lat: Double,
    val lon: Double
)

@JsonClass(generateAdapter = true)
data class MainDto(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val pressure: Int,
    val humidity: Int,
    val sea_level: Int?,
    val grnd_level: Int?
)

@JsonClass(generateAdapter = true)
data class CityDto(
    val id: Long,
    val name: String,
    val coord: CoordDto,
    val main: MainDto,
    val dt: Long,
    val wind: WindDto,
    val sys: SysDto,
    val rain: Any?,   // or you can create a RainDto if you know the structure
    val snow: Any?,   // likewise for snow
    val clouds: CloudsDto,
    val weather: List<OpenWeatherDto>
)

@JsonClass(generateAdapter = true)
data class LocationsResponseDto(
    val message: String,
    val cod: String,
    val count: Int,
    val list: List<CityDto>
)