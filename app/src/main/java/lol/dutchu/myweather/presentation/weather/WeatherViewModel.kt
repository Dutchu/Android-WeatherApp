package lol.dutchu.myweather.presentation.weather

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.launch
import lol.dutchu.myweather.domain.location.LocationData
import lol.dutchu.myweather.domain.location.LocationTracker
import lol.dutchu.myweather.domain.repository.LocationRepository
import lol.dutchu.myweather.domain.repository.WeatherRepository
import lol.dutchu.myweather.domain.util.Resource

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val weatherRepository: WeatherRepository,
    private val locationTracker: LocationTracker,
    private val locationRepository: LocationRepository,
): ViewModel() {

    var state by mutableStateOf(WeatherState())
        private set

    fun onSearchQueryChange(query: String) {
        state = state.copy(searchQuery = query)
        if (query.length >= 3) {
            searchLocations(query)
        } else {
            state = state.copy(searchResults = emptyList(), isSearching = false)
        }
    }

    // Toggle between GPS and search-based location
    fun toggleLocationSource(useGps: Boolean) {
        state = state.copy(useGpsLocation = useGps)

        if (useGps) {
            // Clear search results and query
            state = state.copy(
                searchResults = emptyList(),
                searchQuery = ""
            )
            // Load weather using GPS
            loadWeatherInfoFromGps()
        }
    }

    // Resolve location name from coordinates for display
//    fun resolveLocationName(latitude: Double, longitude: Double) {
//        viewModelScope.launch {
//            when (val result = locationRepository.getLocatioName(latitude, longitude)) {
//                is Resource.Success -> {
//                    result.data?.let { locationName ->
//                        state = state.copy(
//                            searchQuery = locationName
//                        )
//                    }
//                }
//                else -> {} // Ignore errors here
//            }
//        }
//    }

    // Load weather info using GPS
    fun loadWeatherInfoFromGps() {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                error = null
            )

            locationTracker.getCurrentLocation()?.let { location ->
                when (val result = weatherRepository.getWeatherInfo(location.latitude, location.longitude)) {
                    is Resource.Success -> {
                        state = state.copy(
                            weatherInfo = result.data,
                            isLoading = false,
                            error = null,
                            // Clear search results when using GPS
                            searchResults = emptyList()
                        )

                        // Optionally: resolve location name for display
//                        resolveLocationName(location.latitude, location.longitude)
                    }
                    is Resource.Error -> {
                        state = state.copy(
                            weatherInfo = null,
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            } ?: run {
                state = state.copy(
                    isLoading = false,
                    error = "Couldn't specify location. Check app's permissions and enable GPS."
                )
            }
        }

    }

    private fun searchLocations(query: String) {
        viewModelScope.launch {
            state = state.copy(isSearching = true)
            when (val result = locationRepository.getLocations(query)) {
                is Resource.Success -> {
                    state = state.copy(
                        searchResults = result.data ?: emptyList(),
                        isSearching = false,
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        isSearching = false,
                        error = result.message
                    )
                }
            }
        }
    }

    fun selectLocation(location: LocationData) {
        viewModelScope.launch {
            state = state.copy(
                isLoading = true,
                searchResults = emptyList(),
                searchQuery = "${location.name}, ${location.country}"
            )

            when (val result = weatherRepository.getWeatherInfo(location.latitude, location.longitude)) {
                is Resource.Success -> {
                    state = state.copy(
                        weatherInfo = result.data,
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        weatherInfo = null,
                        isLoading = false,
                        error = result.message
                    )
                }
            }
        }
    }
    fun selectDay(dayIndex: Int) {
        state = state.copy(selectedDayIndex = dayIndex)
    }

//    fun loadWeatherInfo() {
//        viewModelScope.launch {
//            state.copy(
//                isLoading = true,
//                error = null
//            )
//            locationTracker
//
//            locationTracker.getCurrentLocation()?.let { location ->
//                when(val result = repository.getLocationData(location.latitude, location.longitude)) {
//                    is Resource.Success -> {
//                        state = state.copy(
//                            weatherInfo = result.data,
//                            isLoading = false,
//                            error = null
//                        )
//                    }
//                    is Resource.Error -> {
//                        state = state.copy(
//                            weatherInfo = null,
//                            isLoading = false,
//                            error = result.message
//                        )
//                    }
//                }
//            } ?: run {
//                state = state.copy(
//                    isLoading = false,
//                    error = "Couldn't specify location. Check app's permissions and enable GPS."
//                )
//            }
//        }
//    }
}