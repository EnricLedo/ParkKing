package com.example.parkingcompose.screens


import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.parkingcompose.viewmodels.SelectLocationViewModel

@Composable
fun SelectLocationScreen(
    selectLocationViewModel: SelectLocationViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var mapView: MapView? = null
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(context, "Permission denied, can't proceed!", Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(key1 = Unit) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        AndroidView(factory = { ctx ->
            MapView(ctx).also {
                it.onCreate(null)
                it.onResume()
                mapView = it
                it.getMapAsync { googleMap ->
                    setupGoogleMap(googleMap, context) { latLng ->
                        selectedLatLng = latLng
                    }
                }
            }
        }, modifier = Modifier.fillMaxSize())

        Button(
            onClick = {
                selectedLatLng?.let {
                    try {
                        selectLocationViewModel.updateSelectedLocation(it)
                        navController.navigate("crearparking")
                    } catch (e: Exception) {
                        Toast.makeText(context, "Navigation error: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                    }
                } ?: Toast.makeText(context, "No location selected", Toast.LENGTH_LONG).show()
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text("Accept Location")
        }
    }
}

fun setupGoogleMap(googleMap: GoogleMap, context: Context, onMapClick: (LatLng) -> Unit) {
    googleMap.uiSettings.isMyLocationButtonEnabled = true
    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        googleMap.isMyLocationEnabled = true
    }

    googleMap.setOnMapClickListener { latLng ->
        googleMap.clear()
        googleMap.addMarker(MarkerOptions().position(latLng))
        onMapClick(latLng)
    }
}
