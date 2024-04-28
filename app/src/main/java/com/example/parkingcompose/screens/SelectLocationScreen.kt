package com.example.parkingcompose.screens


import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import com.example.parkingcompose.R
import com.example.parkingcompose.viewmodels.SelectLocationViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

@Composable
fun SelectLocationScreen(
    selectLocationViewModel: SelectLocationViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    var selectedLatLng by remember { mutableStateOf<LatLng?>(null) }

    val mapView = remember {
        MapView(context).apply {
            onCreate(null)
            onResume()
        }
    }

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            // Permission is granted. Continue the action or workflow in your app.
        } else {
            // Explain to the user that the feature is unavailable because the features requires a permission that the user has denied.
        }
    }

    if (ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    } else {
        mapView.getMapAsync { googleMap ->
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isMyLocationButtonEnabled = true

            googleMap.setOnMapClickListener { latLng ->
                googleMap.clear()
                googleMap.addMarker(MarkerOptions().position(latLng))
                selectedLatLng = latLng
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        // Mostrar el MapView en la interfaz de usuario Compose
        AndroidView({ mapView }) { mapView ->
            mapView.onResume()
        }

        // Botón para aceptar la ubicación seleccionada y volver a CreateParkingScreen
        Button(
            onClick = {
                if (selectedLatLng != null) {
                    selectLocationViewModel.updateSelectedLocation(selectedLatLng!!)
                    navController.navigate("crearparking")
                }
            },
            modifier = Modifier
                .width(150.dp)
                .height(80.dp)
                .padding(16.dp)
        ) {
            Text(stringResource(id = R.string.accept))
        }
    }
}
