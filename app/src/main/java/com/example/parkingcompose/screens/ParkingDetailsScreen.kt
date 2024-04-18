package com.example.parkingcompose.screens


import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.parkingcompose.data.Parking
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.ui.theme.OrangeDark
import com.example.parkingcompose.ui.theme.OrangeLight
import com.example.parkingcompose.viewmodels.ParkingDetailsViewModel
import com.example.parkingcompose.viewmodels.ParkingViewModel


@Composable
fun ParkingDetailsScreen(
    parkingId: String,
    navController: NavHostController,
    parkingDetailsViewModel: ParkingDetailsViewModel = viewModel()
) {
    val parking by parkingDetailsViewModel.parking.collectAsState(null)

    LaunchedEffect(parkingId) {
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
                    style = MaterialTheme.typography.displaySmall.copy(color = OrangeDark),
                    modifier = Modifier.padding(top = 8.dp)
                )
                Image(
                    painter = rememberImagePainter(parking!!.image),
                    contentDescription = "Parking Image",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                )
                Text(
                    text = "Rating: ${parking!!.parkingRating}",
                    style = ButtonTextStyle,
                    modifier = Modifier
                        .padding(top = 8.dp, start = 8.dp, end = 8.dp)
                )
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