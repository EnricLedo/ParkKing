package com.example.parkingcompose.dao


import com.example.parkingcompose.model.Review
import com.google.firebase.firestore.FirebaseFirestore

class ReviewDaoImpl : ReviewDao {
    private val db = FirebaseFirestore.getInstance()
    private val reviewsCollection = db.collection("reviews")

    override fun loadReviews(callback: (List<Review>) -> Unit) {
        val reviewsList = mutableListOf<Review>()
        reviewsCollection.get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot) {
                    val review = document.toObject(Review::class.java)
                    reviewsList.add(review)
                }
                callback(reviewsList)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }

    override fun addReview(review: Review) {
        reviewsCollection.add(review)
            .addOnSuccessListener {
                // Handle success if needed
            }
            .addOnFailureListener {
                // Handle error if needed
            }
    }

    override fun updateReview(review: Review) {
        review.id?.let {
            reviewsCollection.document(it).set(review)
                .addOnSuccessListener {
                    // Handle success if needed
                }
                .addOnFailureListener {
                    // Handle error if needed
                }
        }
    }

    override fun deleteReview(reviewId: String) {
        reviewsCollection.document(reviewId).delete()
            .addOnSuccessListener {
                // Handle success if needed
            }
            .addOnFailureListener {
                // Handle error if needed
            }
    }
    override fun searchReviewsByRating(rating: Float, callback: (List<Review>) -> Unit) {
        val reviewsList = mutableListOf<Review>()
        reviewsCollection.whereEqualTo("review_rating", rating)
            .get()
            .addOnSuccessListener { snapshot ->
                for (document in snapshot) {
                    val review = document.toObject(Review::class.java)
                    reviewsList.add(review)
                }
                callback(reviewsList)
            }
            .addOnFailureListener {
                callback(emptyList())
            }
    }
}
