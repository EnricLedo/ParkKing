package com.example.parkingcompose.screens

import android.annotation.SuppressLint
import com.example.parkingcompose.viewmodels.ParkingViewModel
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.basicMarquee
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.ui.theme.OrangeDark
import com.example.parkingcompose.ui.theme.OrangeLight
import com.example.parkingcompose.viewmodels.CreateParkingViewModel
import com.example.parkingcompose.viewmodels.ModerateViewModel

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun ParkingListScreen(
    parkingViewModel: ParkingViewModel = viewModel(),
    createParkingViewModel: CreateParkingViewModel = viewModel(),
    moderateViewModel: ModerateViewModel = viewModel(),
    navController: NavHostController
){
    val parkingListState = parkingViewModel.parkingList.collectAsState()
    val errorState = parkingViewModel.error.collectAsState()

    // Fetch the parking list when ParkingScreen appears
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

    val parkingList = parkingListState.value

    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        Column {
            Button(
                onClick = { navController.navigate("crearparking") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = ButtonColors(
                    containerColor = OrangeDark,
                    contentColor = Color.Unspecified,
                    disabledContainerColor = Color.Unspecified,
                    disabledContentColor = Color.Unspecified
                )
            ) {
                Text("ADD NEW PARKING", style = ButtonTextStyle)
            }

            if (errorState.value != null) {
                Text("Error: ${errorState.value}")
            }

            LazyColumn {
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
fun ParkingItem(
    parking: Parking,
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
                        ParkingName(parking.name,
                            Modifier
                                .weight(1f)
                                .padding(start = 2.dp))
                        ParkingItemButton(
                            expanded = expanded,
                            onClick = { expanded = !expanded }
                        )
                    }
                    val exampleList = listOf("Free", "Open now", "+4 Stars", "Electric", "Motorcycles", "24/7", "Wasteland")
                    LazyRow(
                        Modifier.padding(2.dp).padding(top = 8.dp),
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
            Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = tagIcon,
                contentDescription = null
            )
            Text(
                text = tagName
            )
        }
    }
}

