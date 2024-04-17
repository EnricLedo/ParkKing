package com.example.parkingcompose.viewmodels

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.data.Parking
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ParkingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _parkingList = MutableStateFlow<List<Parking>>(emptyList())
    val parkingList: StateFlow<List<Parking>> = _parkingList

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        getParkingList()
    }

    fun getParkingList() {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("parkings").get().await()
                val list = querySnapshot.documents.mapNotNull { document ->
                    document.toObject(Parking::class.java)
                }
                _parkingList.value = list
            } catch (e: Exception) {
                _error.value = "Error al cargar la lista de parkings: ${e.message}"
            }
        }
    }
    suspend fun getParkingById(id: String): Parking? {
        return try {
            val querySnapshot = db.collection("parkings").whereEqualTo("id", id).get().await()
            val parking = querySnapshot.documents.mapNotNull { document ->
                document.toObject(Parking::class.java)
            }.firstOrNull()

            parking // Returns the Parking object
        } catch (e: Exception) {
            Log.e(TAG, "Error al cargar el parking: ${e.message}")
            null
        }
    }

    fun updateParkingList() {
        getParkingList()
    }

}