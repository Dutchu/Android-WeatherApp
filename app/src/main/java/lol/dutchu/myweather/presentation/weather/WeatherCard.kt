package lol.dutchu.myweather.presentation.weather

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lol.dutchu.myweather.domain.location.LocationData
import lol.dutchu.myweather.domain.weather.WeatherData
import lol.dutchu.myweather.domain.weather.WeatherInfo
import java.time.format.DateTimeFormatter

@Composable
fun WeatherScreen(viewModel: WeatherViewModel) {
    val state = viewModel.state
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    // Effect to load GPS location data on initial launch or toggle
    LaunchedEffect(state.useGpsLocation) {
        if (state.useGpsLocation && state.weatherInfo == null) {
            viewModel.loadWeatherInfoFromGps()
        }
    }

    Scaffold(
        scaffoldState = scaffoldState
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Location search section with toggle
                SearchBar(
                    searchQuery = state.searchQuery,
                    onQueryChange = { viewModel.onSearchQueryChange(it) },
                    useGpsLocation = state.useGpsLocation,
                    onToggleLocationSource = { viewModel.toggleLocationSource(it) }
                )

                // Search results - only show when not using GPS
                if (!state.useGpsLocation && state.searchResults.isNotEmpty()) {
                    LocationSearchResults(
                        locations = state.searchResults,
                        onLocationSelected = { viewModel.selectLocation(it) }
                    )
                } else if (state.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                } else if (state.error != null) {
                    Text(
                        text = state.error,
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                // Weather data display - same as before
                state.weatherInfo?.let { info ->
                    Spacer(modifier = Modifier.height(16.dp))

                    // Day selector (for the week view)
                    DaySelector(
                        weatherInfo = info,
                        selectedDay = state.selectedDayIndex,
                        onDaySelected = { viewModel.selectDay(it) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Current selected day weather
                    val selectedDayData = info.weatherDataPerDay[state.selectedDayIndex] ?: emptyList()
                    if (selectedDayData.isNotEmpty()) {
                        WeatherCard(
                            data = if (state.selectedDayIndex == 0) info.currentWeatherData else selectedDayData[selectedDayData.size / 2],
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Hourly forecast for selected day
                        HourlyWeatherDisplay(
                            weatherData = selectedDayData,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                        )
                    }
                }
            }
        }
    }
}

// Updated SearchBar component with toggle button
@Composable
fun SearchBar(
    searchQuery: String,
    onQueryChange: (String) -> Unit,
    useGpsLocation: Boolean,
    onToggleLocationSource: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Location source toggle
        Switch(
            checked = useGpsLocation,
            onCheckedChange = onToggleLocationSource,
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.primary,
                uncheckedThumbColor = MaterialTheme.colors.background
            ),
            modifier = Modifier.padding(end = 8.dp)
        )

        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange,
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(if (useGpsLocation) "Using GPS location..." else "Search location...")
            },
            leadingIcon = {
                Icon(
                    imageVector = if (useGpsLocation)
                        Icons.Default.LocationOn else Icons.Default.Search,
                    contentDescription = if (useGpsLocation) "GPS location" else "Search"
                )
            },
            enabled = !useGpsLocation, // Disable when using GPS
            singleLine = true,
            shape = RoundedCornerShape(8.dp)
        )
    }
}

@Composable
fun LocationSearchResults(
    locations: List<LocationData>,
    onLocationSelected: (LocationData) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = 4.dp,
        shape = RoundedCornerShape(8.dp)
    ) {
        LazyColumn(
            modifier = Modifier.heightIn(max = 200.dp)
        ) {
            items(locations) { location ->
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLocationSelected(location) }
                        .padding(12.dp)
                ) {
                    Text(
                        text = location.name,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = location.country,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                Divider()
            }
        }
    }
}

@Composable
fun DaySelector(
    weatherInfo: WeatherInfo,
    selectedDay: Int,
    onDaySelected: (Int) -> Unit
) {
    val dayNames = listOf("Today", "Tomorrow", "Day 3", "Day 4", "Day 5", "Day 6", "Day 7")

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(weatherInfo.weatherDataPerDay.keys.sorted()) { dayIndex ->
            val isSelected = dayIndex == selectedDay

            val dayName = if (dayIndex < dayNames.size) dayNames[dayIndex] else "Day ${dayIndex + 1}"
            val data = weatherInfo.weatherDataPerDay[dayIndex]?.firstOrNull()

            Card(
                modifier = Modifier
                    .width(80.dp)
                    .height(100.dp)
                    .clickable { onDaySelected(dayIndex) },
                backgroundColor = if (isSelected) MaterialTheme.colors.primary else MaterialTheme.colors.surface,
                elevation = if (isSelected) 8.dp else 2.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = dayName,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isSelected) Color.White else Color.Black
                    )

                    data?.let {
                        Icon(
                            painter = painterResource(id = it.weatherType.iconRes),
                            contentDescription = it.weatherType.weatherDesc,
                            tint = if (isSelected) Color.White else Color.Unspecified
                        )

                        Text(
                            text = "${it.temperatureCelsius.toInt()}°C",
                            color = if (isSelected) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherCard(
    data: WeatherData?,
    modifier: Modifier = Modifier
) {
    data?.let {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(16.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = it.time.format(DateTimeFormatter.ofPattern("EEEE, MMMM d")),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = it.weatherType.iconRes),
                            contentDescription = it.weatherType.weatherDesc,
                            modifier = Modifier.size(80.dp)
                        )
                        Text(
                            text = it.weatherType.weatherDesc,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "${it.temperatureCelsius.toInt()}°C",
                        fontSize = 50.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    WeatherDataDisplay(
                        value = it.humidity.toInt(),
                        unit = "%",
                        title = "Humidity"
                    )
                    WeatherDataDisplay(
                        value = it.pressure.toInt(),
                        unit = "hPa",
                        title = "Pressure"
                    )
                    WeatherDataDisplay(
                        value = it.windSpeed.toInt(),
                        unit = "km/h",
                        title = "Wind"
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherDataDisplay(
    value: Int,
    unit: String,
    title: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "$value$unit",
            fontWeight = FontWeight.Bold
        )
        Text(
            text = title,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
fun HourlyWeatherDisplay(
    weatherData: List<WeatherData>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        elevation = 4.dp,
        shape = RoundedCornerShape(16.dp)
    ) {
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(weatherData) { data ->
                HourlyWeatherItem(data = data)
            }
        }
    }
}

@Composable
fun HourlyWeatherItem(data: WeatherData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.height(120.dp)
    ) {
        Text(
            text = data.time.format(DateTimeFormatter.ofPattern("HH:mm")),
            fontSize = 14.sp
        )

        Icon(
            painter = painterResource(id = data.weatherType.iconRes),
            contentDescription = data.weatherType.weatherDesc,
            modifier = Modifier.size(40.dp)
        )

        Text(
            text = "${data.temperatureCelsius.toInt()}°C",
            fontWeight = FontWeight.Bold
        )
    }
}