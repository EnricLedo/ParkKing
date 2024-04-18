package com.example.parkingcompose.model

import java.util.Date

data class Review(
    val review_id: Int,
    var review_rating: Float,
    val comment: String,
    val title: String,
    val user_email: String,
    val date: Date,
    val parking_id: Int
)
