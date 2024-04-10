package com.example.parkingcompose.data


data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class Parking(
    val parkingId: Int = 0,
    val location: Location = Location(),
    val name: String = "",
    val description: String = "",
    val image: String = "", // URL del Storage
    val parkingRating: Float = 0.0f,
    val reviewList: List<Review> = emptyList(),
    val tagList: List<Tag> = emptyList(),
    val priceMinute: Float = 0.0f
)