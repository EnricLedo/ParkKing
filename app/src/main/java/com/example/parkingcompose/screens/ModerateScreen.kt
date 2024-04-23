package com.example.parkingcompose.screens

import android.annotation.SuppressLint
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.parkingcompose.model.Parking
import com.example.parkingcompose.navegacion.BottomNavigationBar
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.ui.theme.OrangeLight
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import com.example.parkingcompose.viewmodels.ModerateViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ModerateScreen(moderateViewModel: ModerateViewModel = viewModel(), createParkingViewModel: CreateParkingViewModel = viewModel(), navController: NavHostController) {
    val parkingListState = moderateViewModel.parkingList.collectAsState()
    val errorState = moderateViewModel.error.collectAsState()

    // Fetch the parking list when ModerateScreen appears
    LaunchedEffect(key1 = Unit) {
        moderateViewModel.getParkingList()
    }

    // Observe the parking added event
    LaunchedEffect(createParkingViewModel.parkingAddedEvent) {
        createParkingViewModel.parkingAddedEvent.collect {
            moderateViewModel.getParkingList()
        }
    }

    val parkingList = parkingListState.value

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        Column {
            if (errorState.value != null) {
                Text("Error: ${errorState.value}")
            }

            LazyColumn {
                items(parkingList) { parking ->

                    if (parking.checked == false) {
                        ParkingModerateItem(
                            parking = parking,
                            moderateViewModel = moderateViewModel,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParkingModerateItem(
    parking: Parking,
    moderateViewModel: ModerateViewModel,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Card(
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
            ) {
                //Column 1
                Column(
                    modifier = Modifier,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    ParkingImage(parking.image)
                    ParkingRating(parking.parkingRating)
                }
                //Column 2
                Column(
                    modifier = Modifier.padding(start = 6.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ParkingModerateInformation(parking.id, parking.name, modifier)
                        ParkingItemButton(
                            expanded = expanded,
                            onClick = { expanded = !expanded }
                        )
                    }
                    val exampleList = listOf("Gratis", "Abierto ahora", "+4 estrellas", "Eléctrico", "Descampado", "Motos")
                    LazyRow(
                        Modifier.padding(8.dp).padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(exampleList) { tag ->
                            TagItem(
                                tagName = tag,
                                tagIcon = Icons.Filled.AccountBox
                            )
                        }
                    }
                }
            }
        }
        if (expanded) {
            ParkingDescription(
                parking.description, modifier = Modifier.padding(
                    start = 16.dp,
                    top = 8.dp,
                    bottom = 16.dp,
                    end = 16.dp
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(0.dp, 0.dp, 8.dp, 12.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { moderateViewModel.denyParking(parking.id) },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Black)

                }

                Button(
                    onClick = {
                        moderateViewModel.enableParking(parking.id, context)
                    },
                    modifier = Modifier.padding(end = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                    border = BorderStroke(1.dp, Color.Black)
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Enable", tint = Color.Black)
                }
            }
        }
    }
}

@Composable
fun ParkingModerateInformation(
    parkingId: String,
    parkingName: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text( text = parkingId)
        Text(
            text = parkingName,
            style = ButtonTextStyle,
            fontSize = 40.sp,
            modifier = Modifier
                .padding(top = 8.dp)

        )
    }
}