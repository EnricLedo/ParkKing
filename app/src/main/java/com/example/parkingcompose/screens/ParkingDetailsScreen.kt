package com.example.parkingcompose.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.parkingcompose.data.Parking
import com.example.parkingcompose.ui.theme.OrangeLight
import com.example.parkingcompose.viewmodels.ParkingViewModel


@Composable
fun ParkingDetailsScreen(
    parkingId: String,
    navController: NavHostController,
    parkingViewModel: ParkingViewModel
) {
    var parking by remember { mutableStateOf<Parking?>(null) }

    LaunchedEffect(parkingId) {
        parking = parkingViewModel.getParkingById(parkingId)
    }
    LazyColumn {
        item {
            Card(
                modifier = Modifier,
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
                        if(parking != null) {
                            Text(
                                text = parking!!.name,
                                style = MaterialTheme.typography.displaySmall.copy(color = Color.Black),
                                modifier = Modifier
                                    .padding(top = 8.dp)
                            )
                            ParkingIcon(parking!!.image)
                        }

                        //ParkingInformation(parking.name, parking.parkingRating, modifier, navController)
                        Spacer(Modifier.weight(1f))

                    }
                    //MOSTRAR LISTA DE TAGS EN UN LAZYROW
                    //MOSTRAR LISTA DE REVIEWS
                }
            }
        }
    }
}
