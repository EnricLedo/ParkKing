package com.example.parkingcompose.data

import androidx.compose.ui.graphics.ImageBitmap


data class Parking(
    val parkingId: Int,
    val location: Pair<Double, Double>,
    val name: String,
    val description: String,
    val image: ImageBitmap,
    val parkingRating: Float,
    val reviewList: List<Review>,
    val tagList: List<Tag>,
    val priceMinute: Float
)