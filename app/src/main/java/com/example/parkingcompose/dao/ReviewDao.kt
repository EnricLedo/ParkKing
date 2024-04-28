package com.example.parkingcompose.dao
import com.example.parkingcompose.model.Review

interface ReviewDao {
        fun loadReviews(callback: (List<Review>) -> Unit)
         fun addReview(review: Review)
         fun updateReview(review: Review)
         fun deleteReview(reviewId: String)
        fun searchReviewsByRating(rating: Float, callback: (List<Review>) -> Unit)

    }
