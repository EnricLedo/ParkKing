package com.example.parkingcompose.screens


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.ui.theme.OrangeDark
import com.example.parkingcompose.viewmodels.ParkingDetailsViewModel


@Composable
fun ParkingDetailsScreen(
    parkingId: String,
    navController: NavHostController,
    parkingDetailsViewModel: ParkingDetailsViewModel = viewModel()
) {
    val parking by parkingDetailsViewModel.parking.collectAsState(null)

    LaunchedEffect(key1 = parkingId) {
        parkingDetailsViewModel.getParkingById(parkingId)
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (parking != null) {
            Column(
                modifier = Modifier
                    .animateContentSize(
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioNoBouncy,
                            stiffness = Spring.StiffnessMedium
                        )
                    )
                    .padding(8.dp)
            ) {
                Text(
                    text = parking!!.name,
                    style = ButtonTextStyle,
                    fontSize = 40.sp,
                    color = OrangeDark,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    textAlign = TextAlign.Center
                )
                Image(
                    painter = rememberImagePainter(parking!!.image),
                    contentDescription = "Parking Image",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween){
                    Text(
                        text = "Rating: ${parking!!.parkingRating}",
                        style = ButtonTextStyle,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                    )
                    Text(
                        text = "Autor: ${parking!!.createdBy}",
                        style = ButtonTextStyle,
                        modifier = Modifier
                            .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                    )
                }
                Button(
                    onClick = { navController.navigate("createReview/${parking!!.id}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Crear Reseña")
                }
                Button(
                    onClick = { navController.navigate("listReviews/${parking!!.id}") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Lista de Reseñas")
                }
                Text(
                    text = parking!!.description,
                    style = ButtonTextStyle,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                )
                Text(
                    text = "Price per minute: ${parking!!.priceMinute}",
                    style = ButtonTextStyle,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                )
            }
        }
    }
}