package lol.dutchu.myweather.presentation

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import dagger.hilt.android.AndroidEntryPoint
import lol.dutchu.myweather.presentation.ui.theme.DarkBlue
import lol.dutchu.myweather.presentation.ui.theme.WeatherAppTheme
import lol.dutchu.myweather.presentation.weather.WeatherScreen
import lol.dutchu.myweather.presentation.weather.WeatherViewModel

@AndroidEntryPoint
class MainActivity : ComponentActivity() {


    private val extendedViewModel: WeatherViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up permission launcher
        permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            // Check if both location permissions are granted
            val locationPermissionsGranted = permissions.entries.all {
                it.key.contains("LOCATION") && it.value
            }

            if (locationPermissionsGranted && extendedViewModel.state.useGpsLocation) {
                extendedViewModel.loadWeatherInfoFromGps()
            }
        }

        // Request location permissions
        permissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ))

        setContent {
            WeatherAppTheme {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(DarkBlue)
                ) {
                    // Use the updated WeatherScreen with location toggle
                    WeatherScreen(viewModel = extendedViewModel)
                }
            }
        }
    }
}