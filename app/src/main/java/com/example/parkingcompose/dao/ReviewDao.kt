package com.example.parkingcompose.dao
import com.example.parkingcompose.data.Review
import com.google.firebase.firestore.FirebaseFirestore

    interface ReviewDao {
        fun loadReviews(callback: (List<Review>) -> Unit)
         fun addReview(review: Review)
         fun updateReview(review: Review)
         fun deleteReview(reviewId: String)

    }
