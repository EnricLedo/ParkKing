package com.example.parkingcompose.screens

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import com.example.parkingcompose.viewmodels.ParkingViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.parkingcompose.model.Parking
import com.example.parkingcompose.navegacion.BottomNavigationBar
import com.example.parkingcompose.ui.theme.BlueGreyDark
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.ui.theme.OrangeLight
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import com.example.parkingcompose.viewmodels.ModerateViewModel
import com.example.parkingcompose.viewmodels.TagViewModel
import com.google.android.gms.maps.model.LatLng

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ParkingListScreen(
    parkingViewModel: ParkingViewModel = viewModel(),
    createParkingViewModel: CreateParkingViewModel = viewModel(),
    moderateViewModel: ModerateViewModel = viewModel(),
    tagViewModel: TagViewModel = viewModel(),
    navController: NavHostController, userLocation: LatLng = LatLng(0.0, 0.0) // Este valor debería ser obtenido dinámicamente
){
    val parkingListState = parkingViewModel.filteredParkings.collectAsState()
    val errorState = parkingViewModel.error.collectAsState()
    var showFilteredParkings by remember { mutableStateOf(false) }
    val tagsState = tagViewModel.getTagsFlow().collectAsState(initial = emptyList())
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    val coroutineScope = rememberCoroutineScope()
    // Fetch the parking list when ParkingScreen appears

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
    LaunchedEffect(key1 = Unit) {
        parkingViewModel.getParkingList()
    }

    // Observe the update event
    LaunchedEffect(createParkingViewModel.parkingAddedEvent) {
        createParkingViewModel.parkingAddedEvent.collect {
            parkingViewModel.getParkingList()
        }
    }

    // Observe the parking enabled event
    LaunchedEffect(moderateViewModel.parkingEnabledEvent) {
        moderateViewModel.parkingEnabledEvent.collect {
            parkingViewModel.getParkingList()
        }
    }

    LaunchedEffect(selectedTags) {
        Log.d(ContentValues.TAG, "Selected tags changed: $selectedTags")
    }

    val parkingList = parkingListState.value

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

            LazyColumn {
                items(parkingList) { parking ->
                    if(parking.checked == true){
                        ParkingItem(
                            parking = parking,
                            parkingViewModel = parkingViewModel,
                            modifier = Modifier.padding(8.dp),
                            navController
                        )
                    }
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
    parkingViewModel: ParkingViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier.clickable { navController.navigate("parkingDetailsScreen/${parking.id}") },
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
                ParkingIcon(parking.image, parking.parkingRating)
                ParkingInformation(parking.name, modifier.weight(1f, fill = false), navController)
                ParkingItemButton(
                    expanded = expanded,
                    onClick = { expanded = !expanded },
                )
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
            }
        }
    }
}

@Composable
fun ParkingItemButton(
    expanded: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
    ) {
        Icon(
            imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
            contentDescription = "See more or less information about a parking",
            tint = MaterialTheme.colorScheme.secondary
        )
    }
}

@Composable
fun ParkingIcon(
    parkingIcon: String,
    parkingRating: Double,
    modifier: Modifier = Modifier
) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = parkingIcon).apply(block = fun ImageRequest.Builder.() {
            transformations(CircleCropTransformation())
        }).build()
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .size(64.dp)
                .padding(8.dp)
                .clip(MaterialTheme.shapes.small)
        )
        Text(
            text = parkingRating.toString(),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ParkingInformation(
    parkingName: String,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    Column(modifier = modifier) {

        Text(
            text = parkingName,
            style = ButtonTextStyle,
            fontSize = 40.sp,
            color = BlueGreyDark,
            modifier = Modifier
                .padding(top = 8.dp)

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
