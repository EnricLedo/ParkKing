package com.example.parkingcompose.screens

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.location.Location
import android.util.Log
import com.example.parkingcompose.viewmodels.ParkingViewModel


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import com.example.parkingcompose.viewmodels.TagViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ParkingListScreen(
    parkingViewModel: ParkingViewModel = viewModel(),
    createParkingViewModel: CreateParkingViewModel = viewModel(),
    tagViewModel: TagViewModel = viewModel(),
    navController: NavHostController, userLocation: LatLng = LatLng(0.0, 0.0) // Este valor debería ser obtenido dinámicamente

) {
    // Estados para controlar la visualización de parkings filtrados
    var showFilteredParkings by remember { mutableStateOf(false) }
    val parkingListState = parkingViewModel.filteredParkings.collectAsState()
    val errorState = parkingViewModel.error.collectAsState()
    val tagsState = tagViewModel.getTagsFlow().collectAsState(initial = emptyList())
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    val coroutineScope = rememberCoroutineScope()


    var minDistance by remember { mutableStateOf(1f) } // Kilómetros
    var maxDistance by remember { mutableStateOf(5f) } // Kilómetros

    LaunchedEffect(minDistance, maxDistance) {
        parkingViewModel.filterParkings(
            userLat = userLocation.latitude,
            userLng = userLocation.longitude,
            minDist = minDistance * 1000,  // Convertir km a metros
            maxDist = maxDistance * 1000   // Convertir km a metros
        )
        showFilteredParkings = true
    }
    // Observe the update event
    LaunchedEffect(createParkingViewModel.parkingAddedEvent) {
        createParkingViewModel.parkingAddedEvent.collect {
            parkingViewModel.getParkingList()
        }
    }

    LaunchedEffect(key1 = Unit) {
        parkingViewModel.getParkingList()
    }


    LaunchedEffect(selectedTags) {
        Log.d(TAG, "Selected tags changed: $selectedTags")
    }


    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) {

        Column {
            ParkingSearchBar(onQueryChanged = parkingViewModel::updateSearchQuery)

            LazyRow {
                items(tagsState.value) { tag ->
                    // Ejemplo de cómo se debe manejar el clic en un tag
                    TagItem(
                        tag = tag.title,
                        isSelected = selectedTags.contains(tag.id),
                        onClick = {
                            // Aquí asumimos que tag.id no es nulo
                            val tagId = tag.id!!
                            selectedTags = if (selectedTags.contains(tagId)) {
                                selectedTags - tagId
                            } else {
                                selectedTags + tagId
                            }
                            parkingViewModel.updateSelectedTags(tagId)
                        }
                    )


                }
            }
            Button(
                onClick = { parkingViewModel.orderParkingsByDistance(true) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Ordenar por Distancia Ascendente")
            }
            Button(
                onClick = { parkingViewModel.orderParkingsByDistance(false) },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Ordenar por Distancia Descendente")
            }
            Button(
                onClick = { parkingViewModel.orderByCreationDate() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("ORDER BY CREATION DATE")
            }
            // Botones para agregar y gestionar parkings y tags
            Button(
                onClick = { navController.navigate("crearparking") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("ADD NEW PARKING", style = ButtonTextStyle)
            }

            Button(
                onClick = { navController.navigate("tagsscreen") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text("MANAGE TAGS", style = ButtonTextStyle)
            }

            if (errorState.value != null) {
                Text("Error: ${errorState.value}")
            }

            // Lista de parkings
            LazyColumn {
                items(parkingListState.value) { parking ->
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
fun TagItem(tag: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface
        )
    ) {
        Text(
            text = tag,
            modifier = Modifier.padding(8.dp),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}


@Composable
fun ParkingSearchBar(onQueryChanged: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    OutlinedTextField(
        value = text,
        onValueChange = {
            text = it
            onQueryChanged(it)
        },
        label = { Text("Search Parkings") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
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



