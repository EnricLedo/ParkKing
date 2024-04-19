package com.example.parkingcompose.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberMarkerState


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapScreen(ViewModel: CreateParkingViewModel, mapViewModel: MapViewModel, navController: NavHostController) {
    val cameraPositionState = rememberCameraPositionState()
    val parkingListState = mapViewModel.parkingList.collectAsState()
    val parkingList = parkingListState.value
    val parkingAddedEvent by ViewModel.parkingAddedEvent.collectAsState(initial = Unit)

    LaunchedEffect(parkingAddedEvent) {
        // Actualiza la lista de parkings
        mapViewModel.getParkingList()
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
                    parkingList.forEach { parking ->
                        val markerState = mapViewModel.rememberCustomMarkerState("${parking.location.latitude},${parking.location.longitude}")
                        Marker(
                            state = markerState,
                            title = parking.name
                        )
                    }
                }

                Button(
                    onClick = {
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
                        contentDescription = "Icono de ubicación",


                        )
                }
            }
        }
    }

}

