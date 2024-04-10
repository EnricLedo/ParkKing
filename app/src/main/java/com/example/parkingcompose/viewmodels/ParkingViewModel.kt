package com.example.parkingcompose.viewmodels

import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import com.example.parkingcompose.data.Parking
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

// Define a StateFlow for parkingList in ParkingViewModel
class ParkingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _parkingList = MutableStateFlow<List<Parking>>(emptyList())
    val parkingList: StateFlow<List<Parking>> = _parkingList

    // Load parking list from Firestore
    suspend fun getParkingList() {
        try {
            val querySnapshot = db.collection("parkings").get().await()
            val list = mutableListOf<Parking>()
            for (document in querySnapshot.documents) {
                val parking = document.toObject(Parking::class.java)
                parking?.let {
                    list.add(it)
                }
            }
            _parkingList.value = list
        } catch (e: Exception) {
            Toast.makeText(
                null,
                "Error al cargar la lista de parkings: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}
