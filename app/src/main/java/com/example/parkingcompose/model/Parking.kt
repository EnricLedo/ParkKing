package com.example.parkingcompose.model


data class Location(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

data class Parking(
    val id: String = "",
    val location: Location = Location(),
    val name: String = "",
    val description: String = "",
    val image: String = "", // URL del Storage
    val parkingRating: Double = 0.0,
    val reviewList: List<Review> = emptyList(),
    val tagList: List<Tag> = emptyList(),
    val priceMinute: Float = 0.0f,
    val checked: Boolean = false,
    val createdBy: String = ""
)