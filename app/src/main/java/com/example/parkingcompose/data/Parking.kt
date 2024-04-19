package com.example.parkingcompose.data

import com.google.firebase.Timestamp


data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class Parking(
    val id: String= "",
    val location: Location = Location(),
    val name: String = "",
    val description: String = "",
    val image: String = "",  // URL from Firebase Storage
    val parkingRating: Float = 0.0f,
    val reviewList: List<Review> = emptyList(),  // Ensure Review also has a no-arg constructor
    val priceMinute: Float = 0.0f,
    var tags: List<String> = emptyList(), // Asegúrate de que 'tags' está definido como una lista mutable
    val createdAt: Timestamp = Timestamp.now()

)
