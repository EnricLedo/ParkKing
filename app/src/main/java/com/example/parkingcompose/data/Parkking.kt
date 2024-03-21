package com.example.parkingcompose.data


data class Parkking(
    val parking_id: Int,
    val location: Pair<Double, Double>,
    val name: String,
    val description: String,
    val image: String,
    val parking_rating: Float,
    val review_list: List<Review>,
    val tag_list: List<Tag>,
    val price_minute: Float
)
