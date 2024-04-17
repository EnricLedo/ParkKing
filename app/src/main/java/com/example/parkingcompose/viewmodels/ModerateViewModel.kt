package com.example.parkingcompose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.dao.ParkingDAO
import com.example.parkingcompose.data.Parking
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ModerateViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _parkingList = MutableStateFlow<List<Parking>>(emptyList())
    val parkingList: StateFlow<List<Parking>> = _parkingList
    private val parkingDao = ParkingDAO()
    private val _parkingEnabledEvent = MutableSharedFlow<Unit>()
    val parkingEnabledEvent: SharedFlow<Unit> = _parkingEnabledEvent

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

    fun deleteParking(parking: Parking) {
        viewModelScope.launch {
            try {
                //db.collection("parkings").document(parking.).delete().await()
                getParkingList()
            } catch (e: Exception) {
                _error.value = "Error al borrar el parking: ${e.message}"
            }
        }
    }

    fun enableParking(id: String) {
        val parkingDocument = db.collection("parkings").document(id)
        parkingDocument.update("checked", true).addOnSuccessListener {
            // After the update is successful, refresh the parking list
            getParkingList()
        }.addOnFailureListener {
            // Handle failure
        }
    }
}