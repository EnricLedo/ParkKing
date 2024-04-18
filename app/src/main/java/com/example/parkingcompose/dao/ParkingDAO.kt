package com.example.parkingcompose.dao


import android.util.Log
import com.example.parkingcompose.data.Parking
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await

class ParkingDAO {
    private val db = FirebaseFirestore.getInstance()

    suspend fun getParkingList(): List<Parking> {
        val querySnapshot = db.collection("parkings").get().await()
        return querySnapshot.documents.mapNotNull { document ->
            document.toObject(Parking::class.java)
        }
    }

    fun denyParking(id: String): Task<Void> {
        val parkingDocument = db.collection("parkings").document(id)
        return parkingDocument.delete()
    }

    fun enableParking(id: String): Task<Void> {
        if (id.isNullOrEmpty()) {
            throw IllegalArgumentException("ID cannot be null or empty")
        }
        val parkingDocument = db.collection("parkings").document(id)
        return parkingDocument.update("checked", true)
    }

    suspend fun getParkingById(id: String): Parking? {
        return try {
            val querySnapshot = db.collection("parkings").whereEqualTo("id", id).get().await()
            val parking = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Parking::class.java)
            }.firstOrNull()

            parking // Returns the Parking object
        } catch (e: Exception) {
            null
        }
    }
}