package com.example.parkingcompose.screens

import ReviewViewModel
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.parkingcompose.R
import com.example.parkingcompose.model.Review
import com.example.parkingcompose.ui.theme.OrangeLight
import com.google.firebase.auth.FirebaseAuth
import java.util.Date


@Composable
fun RatingBarStyle(onRatingChanged: (Float) -> Unit) {
    var rating by remember { mutableStateOf(0f) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        (0..4).forEach { index ->
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        rating = index + 1f
                        onRatingChanged(rating)
                    }
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            color = if (rating > index) MaterialTheme.colorScheme.primary else Color.Gray,
                            shape = MaterialTheme.shapes.small
                        )
                )
            }
        }
    }
}

@Composable
fun RatingBar(rating: Float, onRatingChanged: (Float) -> Unit) {
    RatingBarStyle(onRatingChanged = onRatingChanged)
}

@Composable
fun ListReviewScreen(
    parkingId: String,
    viewModel: ReviewViewModel,
    navController: NavController
) {

    BackHandler {
        // Minimiza la aplicación
        navController.navigate("parkingDetailsScreen/$parkingId")
    }
    // Cargar las reseñas cuando la pantalla sea visible
    LaunchedEffect(Unit) {
        viewModel.loadReviews()
    }
    // Campo de entrada para la puntuación
    var ratingInput by remember { mutableStateOf("") }
    val reviews by viewModel.reviews.collectAsState()

    var sortOrder by remember { mutableStateOf(false) }
    fun sortReviews() {
        reviews?.let { reviewsList ->
            viewModel.setReviews(if (sortOrder) reviewsList.sortedByDescending { it.review_rating } else reviewsList.sortedBy { it.review_rating })
        }
        sortOrder = !sortOrder // Cambiar el orden para el próximo clic
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = ratingInput,
                onValueChange = { ratingInput = it },
                label = { Text("Introduce la puntuación a buscar") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Button(
                onClick = { ratingInput.toFloatOrNull()?.let { viewModel.searchReviewsByRating(it) } },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text("Buscar por puntuación")
            }
            Button(
                onClick = { sortReviews() },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(if (sortOrder) "Ordenar de mayor a menor" else "Ordenar de menor a mayor")
            }

            reviews?.let { reviewsList ->
                // Filtrar las reseñas que coinciden con el parkingId
                val filteredReviews = reviewsList.filter { it.parking_id == parkingId }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    filteredReviews.groupBy { it.user_email }.forEach { (user, userReviews) ->
                        // Mostrar el nombre del usuario como encabezado
                        item {
                            Text(
                                text = "Reseñas:",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(userReviews) { review ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .border(1.dp, Color.Black)
                                    .background(OrangeLight)
                            ) {
                                ReviewItem(review)
                            }
                        }
                    }
                }
            }

        }
    }
}

    @Composable
fun ReviewItem(review: Review) {
    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        Text(text = "Title: " ,fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = review.title ?: "", color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Rating : " ,fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = "${review.review_rating}", color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Comment: " ,fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = review.comment ?: "", color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Date: " ,fontWeight = FontWeight.Bold, color = Color.Black)
        Text(text = review.date?.toString() ?: "", color = Color.Black)
        Spacer(modifier = Modifier.height(4.dp))
    }
}


@Composable
fun CreateReviewScreen(
    parkingId: String, // Recuperar el ID del estacionamiento de la ruta
    navController: NavController,
    viewModel: ReviewViewModel
){
    val context = LocalContext.current
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }

    val reviewSubmittedText = stringResource(id = R.string.review_submitted)
    val errorSendingReviewText = stringResource(id = R.string.error_sending_review)


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = stringResource(id = R.string.leave_your_review))
        RatingBar(
            rating = rating,
            onRatingChanged = { newRating -> rating = newRating }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = title,
            onValueChange = { newTitle -> title = newTitle },
            label = { Text(stringResource(id = R.string.title)) },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = comment,
            onValueChange = { newComment -> comment = newComment },
            label = { Text(stringResource(id = R.string.comment)) },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                val review = Review(
                    review_rating = rating,
                    comment = comment,
                    title = title,
                    user_email = FirebaseAuth.getInstance().currentUser?.email ?: "",
                    date = Date(),
                    parking_id = parkingId // Usar el ID del estacionamiento para el parking_id de la reseña
                )
                try {
                    viewModel.addReview(review)
                    Toast.makeText(context, reviewSubmittedText, Toast.LENGTH_SHORT).show()
                    navController.navigate("listReviews/$parkingId?rating=${review.review_rating}")
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "$errorSendingReviewText ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(stringResource(id = R.string.send))
        }
    }
}







