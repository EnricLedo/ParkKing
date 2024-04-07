package com.example.parkingcompose.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
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
import com.google.maps.android.compose.rememberCameraPositionState
import com.example.parkingcompose.navegacion.BottomNavigationBar
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MapScreen(mapViewModel: MapViewModel, navController: NavHostController) {
    val cameraPositionState = rememberCameraPositionState()

    DaleComposeTheme {
        Scaffold(
            bottomBar = { BottomNavigationBar(navController) }
        ) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.White)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
                    cameraPositionState = cameraPositionState,
                    // Resto de las propiedades del mapa
                )

                Button(
                    onClick = {
                        mapViewModel.currentLocation?.let { myLocation ->
                            cameraPositionState.position = CameraPosition.fromLatLngZoom(myLocation, 16f)
                        }
                    },
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .padding(bottom = 88.dp) // Ajusta este valor según el tamaño de tu BottomNavigationBar
                        .alpha(0.8f)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_my_location),
                        contentDescription = "Icono de ubicación"
                    )
                }
            }
        }
    }
}

