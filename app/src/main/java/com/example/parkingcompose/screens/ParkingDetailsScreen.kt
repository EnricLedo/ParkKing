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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.parkingcompose.R
import com.example.parkingcompose.dao.TagDAO
import com.example.parkingcompose.dao.UserDao
import com.example.parkingcompose.model.Tag
import com.example.parkingcompose.ui.theme.ButtonTextStyle
import com.example.parkingcompose.ui.theme.OrangeDark
import com.example.parkingcompose.ui.theme.OrangeLight
import com.example.parkingcompose.viewmodels.ParkingDetailsViewModel
import okhttp3.internal.userAgent


@Composable
fun ParkingDetailsScreen(
    parkingId: String,
    navController: NavHostController,
    parkingDetailsViewModel: ParkingDetailsViewModel = viewModel(),
    username: String,
    userIsAdmin: Boolean
) {

    Modifier.background(OrangeLight)
    val parking by parkingDetailsViewModel.parking.collectAsState(null)
    val tagDAO = TagDAO()
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
            disabledContentColor = Color.Unspecified
        ),
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
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 10.dp),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Rating: ${parking!!.parkingRating}",
                        color = OrangeDark,
                        style = ButtonTextStyle,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "Star Icon",
                        modifier = Modifier
                            .size(12.dp)
                            .align(Alignment.CenterVertically)
                    )
                }
                Image(
                    painter = rememberAsyncImagePainter(parking!!.image),
                    contentDescription = R.string.parking_image.toString(),
                    modifier = Modifier
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable {
                            navController.navigate("mapa/${parking!!.location.latitude}/${parking!!.location.longitude}")
                        }
                        .align(Alignment.CenterHorizontally)
                )

                val parkingTags = parking!!.tags
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
                        TagItemExpanded(
                            tag = tag
                        )
                    }
                }

                Card(
                    colors = CardColors(
                        containerColor = OrangeLight,
                        contentColor = OrangeDark,
                        disabledContainerColor = Color.Unspecified,
                        disabledContentColor = Color.Unspecified
                    ),
                    modifier = Modifier.padding(6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.author) + " " + (parking!!.createdBy),
                        color = OrangeDark,
                        style = ButtonTextStyle,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                    Text(
                        text =stringResource(R.string.price_minute) + " %.4f".format(parking!!.priceMinute) + " €",
                        color = OrangeDark,
                        style = ButtonTextStyle
                    )
                }

                Text(
                    text = parking!!.description,
                    style = ButtonTextStyle,
                    modifier = Modifier
                        .padding(start = 8.dp, end = 8.dp),
                    color = Color.Black
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
                            stringResource(R.string.reviews_capital_letters),
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
                            stringResource(R.string.new_review_capital_letters),
                            color = Color.White,
                            style = ButtonTextStyle
                        )
                    }
                }
                if(username == parking!!.createdBy || userIsAdmin) {
                    Button(
                        onClick = { navController.navigate("editparking/${parking!!.id}") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        colors = ButtonColors(
                            containerColor = OrangeDark,
                            contentColor = Color.Unspecified,
                            disabledContainerColor = Color.Unspecified,
                            disabledContentColor = Color.Unspecified
                        )
                    ) {
                        Text(
                            stringResource(R.string.edit_parking_capital_letters),
                            color = Color.White,
                            style = ButtonTextStyle
                        )
                    }
                    Button(
                        onClick = {  },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        colors = ButtonColors(
                            containerColor = OrangeDark,
                            contentColor = Color.Unspecified,
                            disabledContainerColor = Color.Unspecified,
                            disabledContentColor = Color.Unspecified
                        )
                    ) {
                        Text(
                            stringResource(R.string.delete_parking),
                            color = Color.White,
                            style = ButtonTextStyle
                        )
                    }
                }
            }
        }
    }
}
