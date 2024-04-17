package com.example.parkingcompose.screens

import android.annotation.SuppressLint
import com.example.parkingcompose.viewmodels.ParkingViewModel


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.parkingcompose.R
import com.example.parkingcompose.data.Parking
import com.example.parkingcompose.navegacion.BottomNavigationBar
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.ui.theme.OrangeLight
import com.example.parkingcompose.ui.theme.OrangeSuperLight
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import com.google.common.math.Quantiles


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ParkingListScreen(parkingViewModel: ParkingViewModel = viewModel(), createParkingViewModel: CreateParkingViewModel = viewModel(), navController: NavHostController) {
    val parkingListState = parkingViewModel.parkingList.collectAsState()
    val errorState = parkingViewModel.error.collectAsState()

    // Observe the update event
    LaunchedEffect(createParkingViewModel.updateEvent) {
        createParkingViewModel.updateEvent.collect {
            parkingViewModel.getParkingList()
        }
    }

    val parkingList = parkingListState.value

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        Column {
            Button(
                onClick = { navController.navigate("crearparking") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("ADD NEW PARKING", style = ButtonTextStyle)
            }

            if (errorState.value != null) {
                Text("Error: ${errorState.value}")
            }

            LazyColumn {
                items(parkingList) { parking ->
                    ParkingItem(
                        parking = parking,
                        parkingViewModel = parkingViewModel,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
            Button(
                onClick = { navController.navigate("createReview") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Crear Reseña")
            }
            Button(
                onClick = { navController.navigate("listReviews") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Volver a la Lista de Reseñas")
            }

        }
    }
}

// ... Resto de los composables ...



@Composable
fun ParkingItem(
    parking: Parking,
    parkingViewModel: ParkingViewModel,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier,
        colors = CardColors(
            containerColor = OrangeLight,
            contentColor = Color.White,
            disabledContainerColor = Color.Unspecified,
            disabledContentColor = Color.Unspecified),

        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                ParkingIcon(parking.image)
                ParkingInformation(parking.name, parking.parkingRating)
                Spacer(Modifier.weight(1f))
                ParkingItemButton(
                    expanded = expanded,
                    onClick = { expanded = !expanded },
                )
            }
            if (expanded) { //Aqui deben ir las reviews
                ParkingDescription(
                    parking.description, modifier = Modifier.padding(
                        start = 16.dp,
                        top = 8.dp,
                        bottom = 16.dp,
                        end = 16.dp
                    )
                )
                AdminButtons(parkingViewModel)
            }
        }
    }
}


@Composable
private fun ParkingItemButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (expanded) Icons.Filled.KeyboardArrowDown else Icons.Filled.KeyboardArrowUp,
            contentDescription = "See more or less information about a parking",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}


@Composable
fun ParkingIcon(
    parkingIcon: String,
    modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = parkingIcon).apply(block = fun ImageRequest.Builder.() {
            transformations(CircleCropTransformation())
        }).build()
    )

    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier
            .size(64.dp)
            .padding(8.dp)
            .clip(MaterialTheme.shapes.small)
    )
}
@Composable
fun ParkingInformation(
    parkingName: String,
    parkingRating: Float,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = parkingName,
            style = MaterialTheme.typography.displaySmall.copy(color = Color.Black),
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = parkingRating.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ParkingDescription(
    parkingDescription: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Text(
            text = "Description",
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = parkingDescription,
            style = MaterialTheme.typography.bodyLarge
        )
    }

}

@Composable
fun AdminButtons(
    parkingViewModel: ParkingViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {

        Button(
            onClick = { /*parkingViewModel.deleteParking(parking = )*/ },
            modifier = Modifier
                .weight(1f)
                .padding(8.dp),
            colors = ButtonColors(
                containerColor = Color.Red,
                contentColor = Color.White,
                disabledContainerColor = Color.Unspecified,
                disabledContentColor = Color.Unspecified),
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
        }
    }
}
