package com.example.parkingcompose.viewmodels

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.dao.ParkingDAO
import com.example.parkingcompose.model.Parking
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ModerateViewModel : ViewModel() {
    private val parkingDao = ParkingDAO()
    private val _parkingList = MutableStateFlow<List<Parking>>(emptyList())
    val parkingList: StateFlow<List<Parking>> = _parkingList
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _parkingEnabledEvent = MutableSharedFlow<Unit>()
    val parkingEnabledEvent: SharedFlow<Unit> = _parkingEnabledEvent

    init {
        getParkingList()
    }

    fun getParkingList() {
        viewModelScope.launch {
            try {
                val list = parkingDao.getParkingList()
                _parkingList.value = list
            } catch (e: Exception) {
                _error.value = "Error al cargar la lista de parkings: ${e.message}"
            }
        }
    }

    fun denyParking(id: String) {
        parkingDao.denyParking(id).addOnSuccessListener {
            // After the deletion is successful, refresh the parking list
            getParkingList()
        }.addOnFailureListener { e ->
            // Handle failure
            _error.value = "Error al eliminar el parking: ${e.message}"
        }
    }

    fun enableParking(id: String, context: Context) {
        parkingDao.enableParking(id).addOnSuccessListener {
            // After the update is successful, refresh the parking list
            getParkingList()
            Toast.makeText(context, "Parking enabled", Toast.LENGTH_SHORT).show()
            viewModelScope.launch {
                _parkingEnabledEvent.emit(Unit) // Emit an update event
            }
        }.addOnFailureListener {
            // Handle failure
        }
    }
}
