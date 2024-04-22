package com.example.parkingcompose.screens


import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.ui.theme.OrangeDark
import com.example.parkingcompose.ui.theme.OrangeLight
import com.example.parkingcompose.viewmodels.ParkingDetailsViewModel


@Composable
fun ParkingDetailsScreen(
    parkingId: String,
    navController: NavHostController,
    parkingDetailsViewModel: ParkingDetailsViewModel = viewModel()
) {
    Modifier.background(OrangeLight)
    val parking by parkingDetailsViewModel.parking.collectAsState(null)

    BackHandler {
        // Minimiza la aplicación
        navController.navigate("parkingList")
    }

        LaunchedEffect(key1 = parkingId) {
        parkingDetailsViewModel.getParkingById(parkingId)
    }

    Card(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        colors = CardColors(
            containerColor = OrangeLight,
            contentColor = Color.White,
            disabledContainerColor = Color.Unspecified,
            disabledContentColor = Color.Unspecified),
        elevation = CardDefaults.cardElevation(4.dp)
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
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    textAlign = TextAlign.Center
                )

                Image(
                    painter = rememberAsyncImagePainter(parking!!.image),
                    contentDescription = "Parking Image",
                    modifier = Modifier
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            navController.navigate("mapa/${parking!!.location.latitude}/${parking!!.location.longitude}")
                        }
                        .align(Alignment.CenterHorizontally)


                )
                Card(
                    colors = CardColors(
                        containerColor = Color.White,
                        contentColor = OrangeDark,
                        disabledContainerColor = Color.Unspecified,
                        disabledContentColor = Color.Unspecified),
                    modifier = Modifier.padding(6.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth().padding(2.dp), horizontalArrangement = Arrangement.SpaceBetween){
                        Text(
                            text = "Rating: ${parking!!.parkingRating}",
                            color = OrangeDark,
                            style = ButtonTextStyle,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                        Text(
                            text = "Price/Minute: ${parking!!.priceMinute}",
                            color = OrangeDark,
                            style = ButtonTextStyle
                        )
                        Text(
                            text = "Autor: ${parking!!.createdBy}",
                            color = OrangeDark,
                            style = ButtonTextStyle,
                            modifier = Modifier.padding(end = 2.dp)
                        )
                    }
                }
                Text(
                    text = parking!!.description,
                    style = ButtonTextStyle,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp)
                )
                Row {
                    Button(
                        onClick = { navController.navigate("listReviews/${parking!!.id}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .weight(0.5f),
                        colors = ButtonColors(
                            containerColor = OrangeDark,
                            contentColor = Color.Unspecified,
                            disabledContainerColor = Color.Unspecified,
                            disabledContentColor = Color.Unspecified
                        )
                    ) {
                        Text(
                            "REVIEWS",
                            color = Color.White,
                            style = ButtonTextStyle
                        )
                    }
                    Button(
                        onClick = { navController.navigate("createReview/${parking!!.id}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .weight(0.5f),
                        colors = ButtonColors(
                            containerColor = OrangeDark,
                            contentColor = Color.Unspecified,
                            disabledContainerColor = Color.Unspecified,
                            disabledContentColor = Color.Unspecified
                        )
                    ) {
                        Text(
                            "CREATE REVIEW",
                            color = Color.White,
                            style = ButtonTextStyle
                        )
                    }
                }
            }
        }
    }
}