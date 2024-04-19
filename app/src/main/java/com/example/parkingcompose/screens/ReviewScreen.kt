package com.example.parkingcompose.screens

import ReviewViewModel
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.parkingcompose.data.Review
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
    viewModel: ReviewViewModel
) {
    // Cargar las reseñas cuando la pantalla sea visible
    LaunchedEffect(Unit) {
        viewModel.loadReviews()
    }

    val reviews by viewModel.reviews.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Verificar si la lista de reseñas no es nula
        reviews?.let { reviewsList ->
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(reviewsList) { review ->
                    ReviewItem(review)
                    Divider()
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
        Text(text = review.title)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = "Rating: ${review.review_rating}")
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = review.comment)
    }
}

@Composable
fun CreateReviewScreen(
    navController: NavController,
    viewModel: ReviewViewModel
) {
    val context = LocalContext.current
    var rating by remember { mutableStateOf(0f) }
    var comment by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var parkingId by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Deja tu reseña")
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = title,
            onValueChange = { newTitle -> title = newTitle },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        RatingBar(
            rating = rating,
            onRatingChanged = { newRating -> rating = newRating }
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = comment,
            onValueChange = { newComment -> comment = newComment },
            label = { Text("Comentario") },
            maxLines = 3,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = userEmail,
            onValueChange = { newUserEmail -> userEmail = newUserEmail },
            label = { Text("Correo electrónico del usuario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = parkingId,
            onValueChange = { newParkingId -> parkingId = newParkingId },
            label = { Text("ID del estacionamiento") },
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
                    parking_id = parkingId
                )
                try {
                    viewModel.addReview(review)
                    Toast.makeText(context, "Reseña enviada", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                } catch (e: Exception) {
                    Toast.makeText(
                        context,
                        "Error al enviar la reseña: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Enviar")
        }
    }
}




