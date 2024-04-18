package com.example.parkingcompose.viewmodels

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.model.LocationRepository
import com.example.parkingcompose.model.Parking
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.MarkerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapViewModel(private val locationRepository: LocationRepository) : ViewModel() {
    var currentLocation by mutableStateOf<LatLng?>(null)
    var cameraPosition by mutableStateOf<CameraPosition?>(null)

    private val db = FirebaseFirestore.getInstance()

    private val _parkingList = MutableStateFlow<List<Parking>>(emptyList())
    val parkingList: StateFlow<List<Parking>> = _parkingList

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        getParkingList()
        getCurrentLocation()
    }

    fun getCurrentLocation() {
        viewModelScope.launch {
            currentLocation = locationRepository.getCurrentLocation()
            currentLocation?.let {
                cameraPosition = CameraPosition.fromLatLngZoom(it, 16f)
            }
        }
    }


    fun getParkingList() {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("parkings").get().await()
                val list = querySnapshot.documents.mapNotNull { document: DocumentSnapshot? ->
                    document?.toObject(Parking::class.java)
                }
                _parkingList.value = list
            } catch (e: Exception) {
                _error.value = "Error al cargar la lista de parkings: ${e.message}"
            }
        }
    }

    @Composable
    public fun rememberCustomMarkerState(
        key: String? = null
    ): MarkerState {
        val positionParts = key?.split(",") ?: listOf("0.0", "0.0")
        val lat = positionParts[0].toDoubleOrNull() ?: 0.0
        val lng = positionParts[1].toDoubleOrNull() ?: 0.0
        val position = LatLng(lat, lng)
        return rememberSaveable(key = key, saver = MarkerState.Saver) {
            MarkerState(position)
        }
    }

    fun requestPermissions(context: Context, permissionLauncher: ActivityResultLauncher<Array<String>>) {
        val permissions = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        when {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            shouldShowRequestPermissionRationale(context as Activity, Manifest.permission.ACCESS_FINE_LOCATION) -> {
                // Show an explanation to the user. After the user sees the explanation, try again to request the permission.
            }
            else -> {
                permissionLauncher.launch(permissions)
            }
        }
    }
}