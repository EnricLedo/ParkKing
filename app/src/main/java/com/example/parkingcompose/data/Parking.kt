package com.example.parkingcompose.data


data class Parking(
    val parkingId: Int = 0,
    val location: Pair<Double, Double> = Pair(0.0, 0.0),
    val name: String = "",
    val description: String = "",
    val image: String = "", // URL del Storage
    val parkingRating: Float = 0.0f,
    val reviewList: List<Review> = emptyList(),
    val tagList: List<Tag> = emptyList(),
    val priceMinute: Float = 0.0f
) {
    constructor() : this(
        0,
        Pair(0.0, 0.0),
        "",
        "",
        "",
        0.0f,
        emptyList(),
        emptyList(),
        0.0f
    )
}
