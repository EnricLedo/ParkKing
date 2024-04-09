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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.parkingcompose.R
import com.example.parkingcompose.data.Parking
import com.example.parkingcompose.navegacion.BottomNavigationBar
import com.example.parkingcompose.ui.theme.ButtonTextStyle


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ParkingListScreen(parkingViewModel: ParkingViewModel = viewModel(), navController: NavHostController) {
    val parkingList = remember { parkingViewModel.getParkingList() }

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

            LazyColumn {
                items(parkingList) { parking ->
                    ParkingItem(
                        parking = parking,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun ParkingItem(
    parking: Parking,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = modifier
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
    val image = painterResource(R.drawable.parking_ninot)
    Image(
        painter = image,
        contentDescription = null,
        modifier = modifier
            .size(8.dp)
            .padding(8.dp)
            .clip(MaterialTheme.shapes.small),
        contentScale = ContentScale.Crop
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
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(top = 8.dp)
        )
        Text(
            text = parkingRating.toString(),
            style = MaterialTheme.typography.bodyLarge
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

