package com.example.parkingcompose.data

import java.util.Date

data class Review(
    var review_rating: Float,
    val comment: String,
    val title: String,
    val user_email: String,
    val date: Date,
    val parking_id: String
)
