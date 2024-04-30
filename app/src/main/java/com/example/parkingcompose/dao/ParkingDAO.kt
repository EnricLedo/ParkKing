package com.example.parkingcompose.dao


import com.example.parkingcompose.model.Parking
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ParkingDAO() {
    lateinit var reviewDao: ReviewDao
    private val db = FirebaseFirestore.getInstance()
    private val parkingsCollection = db.collection("parkings")

    suspend fun getParkingList(): List<Parking> {
        val querySnapshot = parkingsCollection.get().await()
        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(Parking::class.java)
        }
    }

    fun denyParking(id: String): Task<Void> {
        val parkingDocument = parkingsCollection.document(id)
        return parkingDocument.delete()
    }

    fun enableParking(id: String): Task<Void> {
        if (id.isNullOrEmpty()) {
            throw IllegalArgumentException("ID cannot be null or empty")
        }
        val parkingDocument = parkingsCollection.document(id)
        return parkingDocument.update("checked", true)
    }

    fun updateParking(updatedParking: Parking): Task<Void> {
        updatedParking.checked = false
        val parkingDocument = parkingsCollection.document(updatedParking.id)
        return parkingDocument.set(updatedParking)
    }

    suspend fun getParkingById(id: String): Parking? {
        return try {
            val querySnapshot = parkingsCollection.whereEqualTo("id", id).get().await()
            val parking = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Parking::class.java)
            }.firstOrNull()

            parking // Returns the Parking object
        } catch (e: Exception) {
            null
        }
    }

    fun updateParkingRating(parkingId: String) {
        // Obtener todas las reseñas del estacionamiento
        reviewDao.loadReviews { reviews ->
            val parkingReviews = reviews.filter { it.parking_id == parkingId }
            if (parkingReviews.isNotEmpty()) {
                // Calcular la calificación media
                val totalRating = parkingReviews.sumOf { it.review_rating?.toDouble() ?: 0.0 }
                val averageRating = totalRating / parkingReviews.size

                // Obtener el estacionamiento y actualizar su parkingRating
                parkingsCollection.document(parkingId).get()
                    .addOnSuccessListener { documentSnapshot ->
                        val parking = documentSnapshot.toObject(Parking::class.java)
                        if (parking != null) {
                            val updatedParking = parking.copy(parkingRating = averageRating)
                            parkingsCollection.document(parkingId).set(updatedParking)
                        }
                    }
            }
        }
    }

    suspend fun updateCreatedBy(oldUsername: String, newUsername: String) {
        val parkings = parkingsCollection.whereEqualTo("createdBy", oldUsername).get().await()
        for (document in parkings.documents) {
            document.reference.update("createdBy", newUsername).await()
        }
    }
}
