package com.example.parkingcompose.data

import java.util.Date
data class Review(
    val id: String? = null,
    val title: String? = null,
    val review_rating: Float? = null,
    val comment: String? = null,
    val user_email: String? = null,
    val date: Date? = null,
    val parking_id: Any? = null // Change the type to Any to handle both Long and String
) {
    constructor() : this(null, null, null, null, null, null, null)
}
