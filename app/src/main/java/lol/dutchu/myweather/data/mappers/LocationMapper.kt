package lol.dutchu.myweather.data.mappers

import lol.dutchu.myweather.data.remote.LocationsResponseDto
import lol.dutchu.myweather.domain.location.LocationData

fun LocationsResponseDto.toLocationDataList(): List<LocationData> {
    return list.map { cityDto ->
        LocationData(
            name = cityDto.name,
            longitude = cityDto.coord.lon,
            latitude = cityDto.coord.lat,
            country = cityDto.sys.country.orEmpty()
        )
    }
}