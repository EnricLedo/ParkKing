
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.dao.ReviewDao
import com.example.parkingcompose.model.Review
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReviewViewModel(private val reviewDao: ReviewDao) : ViewModel() {

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
    }

    fun updateReview(review: Review) {
        reviewDao.updateReview(review)
    }

    fun deleteReview(reviewId: String) {
        reviewDao.deleteReview(reviewId)
    }

    // Función para buscar reseñas por puntuación
    fun searchReviewsByRating(rating: Float) {
        reviewDao.searchReviewsByRating(rating) { reviews ->
            _reviews.value = reviews
        }
    }
}
