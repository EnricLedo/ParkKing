package com.example.parkingcompose.model

import com.google.firebase.Timestamp


data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class Parking(
    val id: String = "",
    val location: Location = Location(),
    var name: String = "",
    var description: String = "",
    var image: String = "", // URL del Storage
    val parkingRating: Double = 0.0,
    val reviewList: List<Review> = emptyList(),
    var priceMinute: Float = 0.0f,
    val checked: Boolean = false,
    val createdBy: String = "",
    var tags: List<String> = emptyList(), // Asegúrate de que 'tags' está definido como una lista mutable
    val createdAt: Timestamp = Timestamp.now()

)
