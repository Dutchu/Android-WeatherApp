package lol.dutchu.myweather.domain.repository

import android.location.Location
import lol.dutchu.myweather.domain.location.LocationData
import lol.dutchu.myweather.domain.util.Resource

interface LocationRepository {
    suspend fun getLocations(cityName: String): Resource<List<LocationData>>
//    suspend fun getLocationName(latitude: Double, longitude: Double): Resource<String>
}