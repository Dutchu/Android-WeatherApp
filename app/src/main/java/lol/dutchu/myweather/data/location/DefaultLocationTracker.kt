package lol.dutchu.myweather.data.location

import android.app.Application
import android.location.Location
import androidx.core.content.ContextCompat
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.suspendCancellableCoroutine
import lol.dutchu.myweather.domain.location.LocationTracker
import javax.inject.Inject
import kotlin.coroutines.resume

class DefaultLocationTracker @Inject constructor(
    private val locationClient: FusedLocationProviderClient,
    private val application: Application
) : LocationTracker {

    @SuppressLint("MissingPermission")
    override suspend fun getCurrentLocation(): Location? {
        Log.d("LocationTracker", "Requesting location...")

        val hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasAccessCoarseLocationPermission = ContextCompat.checkSelfPermission(
            application,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val locationManager = application.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)

        if (!hasAccessCoarseLocationPermission || !hasAccessFineLocationPermission || !isGpsEnabled) {
            Log.d("LocationTracker", "Location permission not granted or GPS is disabled")
            return null
        }

        return suspendCancellableCoroutine { cont ->
            locationClient.getCurrentLocation(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, null
            ).apply {
                if (isComplete) {
                    if (isSuccessful) {
                        Log.d("LocationTracker", "Received location: ${result?.latitude}, ${result?.longitude}")
                        cont.resume(result)
                    } else {
                        Log.e("LocationTracker", "Failed to retrieve location")
                        cont.resume(null)
                    }
                    return@suspendCancellableCoroutine
                }
                addOnSuccessListener {
                    Log.d("LocationTracker", "Location obtained: ${it?.latitude}, ${it?.longitude}")
                    cont.resume(it)
                }
                addOnFailureListener { exception ->
                    Log.e("LocationTracker", "Failed to get location", exception)
                    cont.resume(null)
                }
                addOnCanceledListener {
                    Log.e("LocationTracker", "Location request was canceled")
                    cont.cancel()
                }
            }
        }
    }
}
