package com.example.parkingcompose.screens

import android.annotation.SuppressLint
import android.content.ContentValues
import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import com.example.parkingcompose.R
import com.example.parkingcompose.dao.TagDAO
import com.example.parkingcompose.model.LocationRepository
import com.example.parkingcompose.model.Parking
import com.example.parkingcompose.model.Tag
import com.example.parkingcompose.navegacion.BottomNavigationBar
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import com.example.parkingcompose.viewmodels.ModerateViewModel
import com.example.parkingcompose.viewmodels.ParkingViewModel
import com.example.parkingcompose.viewmodels.TagViewModel


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ParkingListScreen(
    parkingViewModel: ParkingViewModel = viewModel(),
    createParkingViewModel: CreateParkingViewModel = viewModel(),
    moderateViewModel: ModerateViewModel = viewModel(),
    tagViewModel: TagViewModel = viewModel(),
    navController: NavHostController,
    locationRepository: LocationRepository // Este valor debería ser obtenido dinámicamente
){
    LaunchedEffect(Unit) {
        val userLocation = locationRepository.getCurrentLocation()
        userLocation?.let {
            parkingViewModel.setUserLocation(it.latitude, it.longitude)
        }
    }
    val errorState = parkingViewModel.error.collectAsState()
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var showSliderDialog by remember { mutableStateOf(false) }
    val selectedDistance by parkingViewModel.selectedDistance.collectAsState()
    val parkingListState = parkingViewModel.filteredParkings.collectAsState()

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
    val context = LocalContext.current

    LaunchedEffect(selectedTags) {
        val logMessage = context.getString(R.string.select_tags_change)
        Log.d(ContentValues.TAG, logMessage)
    }



    Scaffold(
        bottomBar = { BottomNavigationBar(navController) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("crearparking") },
                containerColor = Color.White,

                modifier = Modifier
                    .border(2.dp, Color.Black, CircleShape)
                    .clip(CircleShape)  // Añade esta línea
            ) {
                Icon(Icons.Filled.Add, contentDescription = R.string.add.toString())
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    )  {
        Column {
            Row(modifier = Modifier.padding(start = 8.dp), verticalAlignment = Alignment.CenterVertically) {
    Button(onClick = { showSliderDialog = true },
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)) {
        Icon(
            painter = painterResource(id = R.drawable.ic_distancefilter),
            contentDescription = stringResource(id = R.string.filter_by_distance),
            tint = Color.White
        )
    }
    ParkingSearchBar(onQueryChanged = parkingViewModel::updateSearchQuery)
}

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

            if (showSliderDialog) {
                var sliderValue by remember { mutableStateOf(selectedDistance ?: 0f) }
                AlertDialog(
                    onDismissRequest = { showSliderDialog = false },
                    title = { Text(stringResource(id = R.string.select_max_distance), color = Color.White)},
                    text = {
                        Column {
                            Text("${stringResource(id = R.string.distance_km)} ${"%.3f".format(sliderValue)} km")
                            Slider(
                                value = sliderValue,
                                onValueChange = { sliderValue = it },
                                valueRange = 0f..50f,
                                steps = 500 // Para tener una precisión de hasta 3 decimales
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            parkingViewModel.setSelectedDistance(sliderValue)
                            parkingViewModel.filterParkingsByDistance()
                            showSliderDialog = false
                        },
                            colors = ButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.Unspecified,
                                disabledContainerColor = Color.Unspecified,
                                disabledContentColor = Color.Unspecified
                            )) {
                            Text(stringResource(id = R.string.apply), color = Color.White)
                        }
                    }
                )
            }

            if (errorState.value != null) {
                Text("${stringResource(id = R.string.error)} ${errorState.value}")
            }

            LazyColumn(
                modifier = Modifier.padding(bottom = 70.dp)
            ) {
                items(parkingListState.value) { parking ->
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
            onClick = { expanded = true },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_order),
                contentDescription = null,
                tint = Color.White
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
                Text(stringResource(id = R.string.sort_by_distance_ascending), color = MaterialTheme.colorScheme.primary)
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    parkingViewModel.orderParkingsByDistance(false) // Descending order
                }
            ) {
                Text(stringResource(id = R.string.sort_by_distance_descending), color = MaterialTheme.colorScheme.primary)
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    parkingViewModel.orderParkingsByBestRating()  // Best rating
                }
            ) {
                Text(stringResource(id = R.string.sort_by_best_rating), color = MaterialTheme.colorScheme.primary)
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    parkingViewModel.orderParkingsByWorstRating() // Worst rating
                }
            ) {
                Text(stringResource(id = R.string.sort_by_worst_rating), color = MaterialTheme.colorScheme.primary)
            }
            DropdownMenuItem(
                onClick = {
                    expanded = false
                    parkingViewModel.orderByCreationDate()
                }
            ) {
                Text(stringResource(id = R.string.sort_by_creation_date), color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}


@Composable
fun RatingFilter(parkingViewModel: ParkingViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val selectedRating by parkingViewModel.selectedRating.collectAsState()

    Box() {
        Button(onClick = { expanded = true }, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)) {
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = stringResource(id = R.string.filter_rating),
                tint = Color.White
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
                        contentDescription = "$rating ${stringResource(id = R.string.start)}",
                        tint = if (rating <= (selectedRating ?: 0)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary                    )
                    Text(" $rating", color = if (rating <= (selectedRating ?: 0)) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSecondary)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
            .padding(16.dp),
        colors = TextFieldDefaults.outlinedTextFieldColors(

            unfocusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.primary
        )
    )
}
@Composable
fun ParkingItem(
    parking: Parking,
    modifier: Modifier = Modifier,
    navController: NavHostController
) {
    var expanded by remember { mutableStateOf(false) }
    val tagDAO = TagDAO()
    Card(
        modifier = modifier.clickable { navController.navigate("parkingDetailsScreen/${parking.id}") },
        elevation = CardDefaults.cardElevation(4.dp),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            disabledContainerColor = Color.Unspecified,
            disabledContentColor = Color.Unspecified)


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
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp),
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
                    var tags by remember { mutableStateOf<List<Tag>>(emptyList()) }

                    LaunchedEffect(parkingTags) {
                        tags = parkingTags.map { tagTitle ->
                            tagDAO.getTagByTitle(tagTitle)!!
                        }
                    }

                    LazyRow(
                        Modifier
                            .padding(2.dp)
                            .padding(top = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(tags) { tag ->
                            TagItem(
                                tag = tag
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
            contentDescription = stringResource(id = R.string.information),
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
            text = String.format("%.2f", parkingRating),
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(end = 2.dp)
        )
        Icon(
            imageVector = Icons.Filled.Star,
            contentDescription = "Star Icon",
            modifier = Modifier.size(12.dp),
            tint = Color.White
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
    tag: Tag
){
    val painter = rememberImagePainter(data = tag.image)
    Image(
        painter = painter,
        contentDescription = null,
        modifier = Modifier
            .size(56.dp)  // Tamaño de la imagen
            .padding(8.dp)  // Padding de la imagen
            .clip(RoundedCornerShape(8.dp))) // Forma de la imagen

}


@Composable
fun ResetSearchButton(parkingViewModel: ParkingViewModel, navController: NavHostController) {
    Button(
        onClick = { parkingViewModel.resetSearch()
            navController.navigate("parkingList") },
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)

    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_resetsearch),
            contentDescription = null,
            tint = Color.White
        )
    }
}

@Composable
fun TagItemExpanded(
    tag: Tag
){
    val painter = rememberImagePainter(data = tag.image)
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            text = { Text(text = tag.content, color = Color.White) },
            confirmButton = {
                Button(onClick = { showDialog = false },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .clip(RoundedCornerShape(1000.dp))
            .clickable { showDialog = true },
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            disabledContainerColor = Color.Unspecified,
            disabledContentColor = Color.Unspecified)

    ) {
        Row(
            Modifier.padding(8.dp),  // Aumenta el padding
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(32.dp)  // Aumenta el tamaño de la imagen
                    .clip(CircleShape)
                    .padding(4.dp)  // Aumenta el padding
            )
            Text(
                text = tag.title,
                modifier = Modifier.padding(4.dp),  // Aumenta el padding
                // Aumenta el tamaño del texto
            )
        }
    }
}
@Composable
fun TagFilterButton(parkingViewModel: ParkingViewModel, tagViewModel: TagViewModel) {
    val tagsState = tagViewModel.getTagsFlow().collectAsState(initial = emptyList())
    var selectedTags by remember { mutableStateOf(setOf<String>()) }
    var showDialog by remember { mutableStateOf(false) }

    Button(onClick = { showDialog = true }, colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary )) {
        Text(stringResource(id = R.string.tags), color = Color.White)
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(stringResource(id = R.string.select_tags), color = Color.White) },
            text = {
                LazyColumn {
                    items(tagsState.value) { tag ->
                        Row(
                            Modifier
                                .clickable {
                                    val tagId = tag.title!!
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
                                checked = selectedTags.contains(tag.title),
                                onCheckedChange = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(tag.title, color = Color.White)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        parkingViewModel.updateSelectedTags(selectedTags)
                    },
                    colors = ButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.Unspecified,
                        disabledContainerColor = Color.Unspecified,
                        disabledContentColor = Color.Unspecified
                    )
                ) {
                    Text(stringResource(id = R.string.apply), color = Color.White)
                }
            }
        )
    }
}

