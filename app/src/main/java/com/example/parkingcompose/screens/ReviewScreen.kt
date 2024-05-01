package com.example.parkingcompose.screens

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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Surface
import androidx.compose.material.TextButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.parkingcompose.R
import com.example.parkingcompose.model.Review
import com.example.parkingcompose.ui.theme.OrangeLight
import com.example.parkingcompose.viewmodels.ReviewViewModel
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
                label = { Text(stringResource(R.string.introduce_la_puntuaci_n_a_buscar)) },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
            )
            Button(
                onClick = { ratingInput.toFloatOrNull()?.let { viewModel.searchReviewsByRating(it) } },
                modifier = Modifier.padding(vertical = 8.dp)
            ) {
                Text(stringResource(R.string.buscar_por_puntuaci_n))
            }
            Button(
                onClick = { sortReviews() },
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Text(if (sortOrder) stringResource(R.string.ordenar_de_mayor_a_menor) else stringResource(
                    R.string.ordenar_de_menor_a_mayor
                )
                )
            }

            reviews?.let { reviewsList ->
                val filteredReviews = reviewsList.filter { it.parking_id == parkingId }
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    filteredReviews.groupBy { it.user_email }.forEach { (user, userReviews) ->

                        items(userReviews) { review ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),

                                colors = CardColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = Color.White,
                                    disabledContainerColor = Color.Unspecified,
                                    disabledContentColor = Color.Unspecified)
                            ) {
                                ReviewItem(review, viewModel = viewModel)
                            }
                        }

                    }
                }
            }
        }

    }
}


@Composable
fun ReviewItem(
    review: Review,
    viewModel: ReviewViewModel // ViewModel
) {
    var showUpdateDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.padding(16.dp)
    ) {

            Text(text = stringResource(id = R.string.title), fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = review.title ?: "", color = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.rating), fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = "${review.review_rating}", color = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = stringResource(id = R.string.comment), fontWeight = FontWeight.Bold, color = Color.Black)
            Text(text = review.comment ?: "", color = Color.Black)


        Spacer(modifier = Modifier.height(4.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically // Alinea los botones verticalmente al centro
        ) {
            // Botón de eliminación
            IconButton(
                onClick = { review.id?.let { viewModel.deleteReview(it) } },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(id = R.string.delet_con)
                )
            }

            // Botón de actualización
            IconButton(
                onClick = { showUpdateDialog = true },
                modifier = Modifier.padding(end = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(id = R.string.edit_con)
                )
            }

            // Mostrar el diálogo de actualización si showUpdateDialog es verdadero
            if (showUpdateDialog) {
                UpdateReviewDialog(
                    review = review,
                    onUpdateReview = { updatedReview ->
                        viewModel.updateReview(updatedReview)
                    },
                    onDismiss = {
                        showUpdateDialog = false
                    }
                )
            }
        }
    }
}
@Composable
fun CreateReviewScreen(
    parkingId: String, // Recuperar el ID del estacionamiento de la ruta
    navController: NavController,
    viewModel: ReviewViewModel
) {
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

@Composable
fun UpdateReviewDialog(
    review: Review,
    onUpdateReview: (Review) -> Unit,
    onDismiss: () -> Unit
) {
    // Estado para almacenar el título y el comentario actualizados
    var updatedTitle by remember { mutableStateOf(review.title ?: "") }
    var updatedComment by remember { mutableStateOf(review.comment ?: "") }

    // Mostrar el diálogo personalizado
    Dialog(
        onDismissRequest = onDismiss
    ) {
        Surface(
            modifier = Modifier.padding(16.dp),
            shape = MaterialTheme.shapes.medium,
            elevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(id = R.string.actualizar_revision),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = updatedTitle,
                    onValueChange = { updatedTitle = it },
                    label = { Text(stringResource(id = R.string.title)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = updatedComment,
                    onValueChange = { updatedComment = it },
                    label = { Text(stringResource(id = R.string.comment)) }
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(stringResource(id = R.string.cancel))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            // Crear una revisión actualizada con los datos ingresados
                            val updatedReview = review.copy(title = updatedTitle, comment = updatedComment)
                            // Llamar a la función de actualización
                            onUpdateReview(updatedReview)
                            // Cerrar el diálogo
                            onDismiss()
                        }
                    ) {
                        Text(stringResource(id = R.string.actualizar))
                    }
                }
            }
        }
    }
}

