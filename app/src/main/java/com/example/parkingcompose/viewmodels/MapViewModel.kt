package com.example.parkingcompose.viewmodels

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.data.LocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MapViewModel(private val locationRepository: LocationRepository) : ViewModel() {
    var currentLocation by mutableStateOf<LatLng?>(null)
    var cameraPosition by mutableStateOf<CameraPosition?>(null)

    init {
        viewModelScope.launch {
            currentLocation = locationRepository.getCurrentLocation()
            currentLocation?.let {
                cameraPosition = CameraPosition.fromLatLngZoom(it, 16f)
            }
        }
    }
}