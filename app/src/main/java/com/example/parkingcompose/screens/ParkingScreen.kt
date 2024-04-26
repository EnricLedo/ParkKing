package com.example.parkingcompose.screens

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import com.example.parkingcompose.viewmodels.ParkingViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.parkingcompose.R
import com.example.parkingcompose.model.Parking
import com.example.parkingcompose.navegacion.BottomNavigationBar
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.ui.theme.OrangeDark
import com.example.parkingcompose.ui.theme.OrangeLight
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import com.example.parkingcompose.viewmodels.ModerateViewModel
import com.example.parkingcompose.viewmodels.TagViewModel
import com.google.android.gms.maps.model.LatLng
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.unit.DpOffset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Dialog
import com.example.parkingcompose.model.Tag
import com.example.parkingcompose.ui.theme.BlueGreyLight


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
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
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
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("crearparking") },
                containerColor = BlueGreyLight,
                contentColor = Color.Unspecified,
                modifier = Modifier
                    .border(2.dp, Color.Black, CircleShape)
                    .clip(CircleShape)  // Añade esta línea
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    )  {
        Column {
            ParkingSearchBar(onQueryChanged = parkingViewModel::updateSearchQuery)
            Row(
                modifier = Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween

                ) {
                YourComposableFunction(parkingViewModel)
                RatingFilter(parkingViewModel)
                TagFilterButton(parkingViewModel, tagViewModel)
                ResetSearchButton(parkingViewModel, navController)
                }

            if (errorState.value != null) {
                Text("Error: ${errorState.value}")
            }

            LazyColumn(
                modifier = Modifier.padding(bottom = 70.dp)
            ) {
                items(parkingList) { parking ->
                    if(parking.checked){
                        ParkingItem(
                            parking = parking,
                            modifier = Modifier.padding(6.dp),
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun YourComposableFunction(parkingViewModel: ParkingViewModel = viewModel()) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        Button(
            onClick = { expanded = true }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_order),
                contentDescription = null
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.align(Alignment.BottomStart)
        ) {
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    parkingViewModel.orderParkingsByDistance(true)  // Ascending order
                }
            ) {
                Text(stringResource(id = R.string.sort_by_distance_ascending))
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    parkingViewModel.orderParkingsByDistance(false) // Descending order
                }
            ) {
                Text(stringResource(id = R.string.sort_by_distance_descending))
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    parkingViewModel.orderParkingsByBestRating()  // Best rating
                }
            ) {
                Text("Ordenar por mejor calificación")
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    parkingViewModel.orderParkingsByWorstRating() // Worst rating
                }
            ) {
                Text("Ordenar por peor calificación")
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    parkingViewModel.orderByCreationDate()
                }
            ) {
                Text("Ordenar por fecha de creacion")
            }
        }
    }
}


@Composable
fun RatingFilter(parkingViewModel: ParkingViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val selectedRating by parkingViewModel.selectedRating.collectAsState()

    Box() {
        Button(onClick = { expanded = true }) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Rating filter",
                tint = MaterialTheme.colorScheme.primary
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            (1..5).forEach { rating ->
                DropdownMenuItem(
                    onClick = {
                        parkingViewModel.setSelectedRating(if (selectedRating == rating) null else rating)
                        expanded = false
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "$rating stars",
                        tint = if (rating <= (selectedRating ?: 0)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                    Text(" $rating")
                }
            }
        }
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
        label = { Text(stringResource(id = R.string.search_parkings)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    )
}
@Composable
fun ParkingItem(
    parking: Parking,
    modifier: Modifier = Modifier,
    navController: NavHostController,
    tagViewModel: TagViewModel = viewModel()
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
                        modifier = Modifier.fillMaxWidth().padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ParkingName(parking.name,
                            Modifier
                                .weight(1f)
                                .padding(start = 2.dp))
                        ParkingItemButton(
                            expanded = expanded,
                            onClick = { expanded = !expanded }
                        )
                    }
                    val parkingTags = parking.tags
                    var tags by remember { mutableStateOf<Tag?>(null) }
                    LazyRow(
                        Modifier
                            .padding(2.dp)
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(parkingTags) { tag ->
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ParkingName(
    parkingName: String,
    modifier: Modifier
) {
    Text(
        text = parkingName,
        fontSize = 34.sp,
        modifier = modifier
            .basicMarquee()
    )
}
@Composable
fun ParkingImage(
    parkingIcon: String
) {
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(LocalContext.current).data(data = parkingIcon).apply(block = fun ImageRequest.Builder.() {
            transformations(CircleCropTransformation())
        }).build()
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier
                .size(90.dp)
                .padding(4.dp)
                .clip(MaterialTheme.shapes.small)
        )
    }
}

@Composable
fun ParkingRating(
    parkingRating: Double,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = parkingRating.toString(),
            style = MaterialTheme.typography.bodyLarge
        )
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Star Icon",
            modifier = Modifier.size(12.dp)
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
            text = stringResource(id = R.string.description),
            style = MaterialTheme.typography.labelSmall
        )
        Text(
            text = parkingDescription,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun TagItem(
    tagName: String,
    tagIcon: ImageVector
){
    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(1000.dp)),
        colors = CardColors(
            containerColor = OrangeDark,
            contentColor = Color.White,
            disabledContainerColor = Color.Unspecified,
            disabledContentColor = Color.Unspecified
        )
    ) {
        Row(
            Modifier.padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = tagIcon,
                contentDescription = null,
                modifier = Modifier
                    .size(16.dp)
            )
            Text(
                text = tagName
            )
        }
    }
}

@Composable
fun ResetSearchButton(parkingViewModel: ParkingViewModel, navController: NavHostController) {
    Button(
        onClick = { parkingViewModel.resetSearch()
            navController.navigate("parkingList") }

    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_resetsearch),
            contentDescription = null
        )
    }
}
@Composable
fun TagFilterButton(parkingViewModel: ParkingViewModel, tagViewModel: TagViewModel) {
    val tagsState = tagViewModel.getTagsFlow().collectAsState(initial = emptyList())
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var showDialog by remember { mutableStateOf(false) }

    Button(onClick = { showDialog = true }) {
        Text("TAGS")
    }

    if (showDialog) {
        Dialog(onDismissRequest = { showDialog = false }) {
            // Agrega un Box alrededor de tu Column y aplica un color de fondo
            Box(modifier = Modifier.background(OrangeLight).border(2.dp, Color.Black)) {
                Column {
                    Text(
                        "Seleccione los tags",
                        modifier = Modifier.padding(8.dp),
                        color = Color.Black, textAlign = TextAlign.Center
                    )
                    LazyColumn {
                        items(tagsState.value.chunked(3)) { rowTags -> // Ajusta el valor en chunked() para cambiar el número de columnas
                            Row(Modifier.padding(8.dp)) {
                                rowTags.forEach { tag ->
                                        Row(
                                            Modifier
                                                .clickable {
                                                    val tagId = tag.id!!
                                                    selectedTags =
                                                        if (selectedTags.contains(tagId)) {
                                                            selectedTags - tagId
                                                        } else {
                                                            selectedTags + tagId
                                                        }
                                                }
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Checkbox(
                                                checked = selectedTags.contains(tag.id),
                                                onCheckedChange = null // Ignoramos este evento ya que manejamos el clic en el Row
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(tag.title, color = Color.Black)
                                        }

                                }
                            }
                        }
                    }
                    Button(
                        onClick = {
                            showDialog = false
                            parkingViewModel.updateSelectedTags(selectedTags)
                        },
                        modifier = Modifier.align(Alignment.End).padding(8.dp)
                    ) {
                        Text("Apply")
                    }
                }
            }
        }
    }
}