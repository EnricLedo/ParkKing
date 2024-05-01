package com.example.parkingcompose.screens

import android.Manifest
import android.annotation.SuppressLint
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.parkingcompose.R
import com.example.parkingcompose.ui.theme.DaleComposeTheme
import com.example.parkingcompose.viewmodels.MapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import com.example.parkingcompose.navegacion.BottomNavigationBar
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.parkingcompose.viewmodels.ModerateViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapScreen(
    createParkingViewModel: CreateParkingViewModel,
    mapViewModel: MapViewModel,
    navController: NavHostController,
    moderateViewModel: ModerateViewModel,
    latitude: Double = 0.0, // Nuevo parámetro
    longitude: Double = 0.0 // Nuevo parámetro
) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    LaunchedEffect(currentRoute) {
        if (currentRoute == "mapa") {
            // Actualiza la lista de parkings
            mapViewModel.getParkingList()
        }
    }
    val parkingEnabledEvent by moderateViewModel.parkingEnabledEvent.collectAsState(initial = false) // Nuevo estado

    LaunchedEffect(parkingEnabledEvent) {
        // Actualiza la lista de parkings
        mapViewModel.getParkingList()
    }
    val context = LocalContext.current
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
        permissions.entries.forEach {
            val isGranted = it.value
            if (isGranted) {
                mapViewModel.getCurrentLocation()
            } else {
                Toast.makeText(context, "${it.key} is denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        mapViewModel.requestPermissions(context, permissionLauncher)
        mapViewModel.getParkingList()
    }

    val cameraPositionState = rememberCameraPositionState()
    val parkingList by mapViewModel.parkingList.collectAsState()
    val parkingAddedEvent by createParkingViewModel.parkingAddedEvent.collectAsState(initial = Unit)

    LaunchedEffect(parkingAddedEvent) {
        // Actualiza la lista de parkings
        mapViewModel.getParkingList()
    }

    LaunchedEffect(latitude, longitude) {
        if (latitude != 0.0 && longitude != 0.0) {
            // Si se proporcionan latitud y longitud, establece la posición de la cámara en estas coordenadas
            mapViewModel.getParkingList()
            cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(latitude, longitude), 16f)
        } else {
            // Si no se proporcionan latitud y longitud, muestra la ubicación actual del usuario
            mapViewModel.currentLocation?.let { myLocation ->
                mapViewModel.getParkingList()
                cameraPositionState.position = CameraPosition.fromLatLngZoom(myLocation, 16f)
            }
        }
    }

    DaleComposeTheme {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.White)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize().padding(bottom = 70.dp),
                    cameraPositionState = cameraPositionState,
                    // Resto de las propiedades del mapa
                ) {
                    DisposableEffect(navBackStackEntry) {
                        // Actualiza la lista de parkings
                        mapViewModel.getParkingList()

                        onDispose { }
                    }

                    parkingList.forEach { parking ->
                        val markerState = mapViewModel.rememberCustomMarkerState("${parking.location.latitude},${parking.location.longitude}")
                        Marker(
                            state = markerState,
                            title = parking.name,
                            snippet = "${stringResource(id = R.string.price)} ${parking.priceMinute} €/min",
                            onInfoWindowClick = {
                                navController.navigate("parkingDetailsScreen/${parking.id}")
                            }
                        )
                    }
                }

                Button(
                    onClick = {
                        // Muestra la ubicación actual del usuario
                        mapViewModel.currentLocation?.let { myLocation ->
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(myLocation, 16f)
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .padding(bottom = 83.dp) // Ajusta este valor según el tamaño de tu BottomNavigationBar
                        .alpha(1f)
                        .width(60.dp),
                    contentPadding = PaddingValues(0.dp)

                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_my_location),
                        contentDescription = stringResource(id = R.string.icon_de_ubicacion),
                    )
                }
            }
        }
    }
}