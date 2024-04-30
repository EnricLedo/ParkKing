package com.example.parkingcompose.viewmodels


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.dao.ParkingDAO
import com.example.parkingcompose.dao.ReviewDao
import com.example.parkingcompose.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(private val reviewDao: ReviewDao,
                      private val parkingDao: ParkingDAO) : ViewModel() {

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews

    init {
        loadReviews() // Llama a loadReviews en la inicialización del ViewModel
    }



    fun loadReviews() {
        viewModelScope.launch {
            reviewDao.loadReviews { reviewsList ->
                setReviews(reviewsList)
            }
        }
    }

    // Método para establecer las reseñas
    fun setReviews(reviews: List<Review>) {
        _reviews.value = reviews
    }

    fun addReview(review: Review) {
        reviewDao.addReview(review)
        loadReviews()
    }

    fun updateReview(review: Review) {
        reviewDao.updateReview(review)
        loadReviews()
    }

    fun deleteReview(reviewId: String) {
    viewModelScope.launch {
        reviewDao.deleteReview(reviewId)
        loadReviews()
    }
}

    // Función para buscar reseñas por puntuación
    fun searchReviewsByRating(rating: Float) {
        reviewDao.searchReviewsByRating(rating) { reviews ->
            _reviews.value = reviews
        }
    }
}

