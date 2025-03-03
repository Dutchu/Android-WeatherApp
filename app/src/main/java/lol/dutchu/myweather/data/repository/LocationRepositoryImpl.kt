package lol.dutchu.myweather.data.repository

import lol.dutchu.myweather.data.mappers.toLocationDataList
import lol.dutchu.myweather.data.remote.LocationApi
import lol.dutchu.myweather.domain.location.LocationData
import lol.dutchu.myweather.domain.repository.LocationRepository
import lol.dutchu.myweather.domain.util.Resource
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val api: LocationApi
) : LocationRepository {
    override suspend fun getLocations(
        cityName: String,
    ): Resource<List<LocationData>> {
        return try {
            val response = api.fetchLocation(cityName)
            val domainList = response.toLocationDataList()
            Resource.Success(domainList)

        } catch (e: Exception) {
            e.printStackTrace()
            Resource.Error(e.message ?: "An unknown error occurred while fetching a Location.")
        }
    }
}
