package com.example.parkingcompose.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.dao.ParkingDAO
import com.example.parkingcompose.model.Parking
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ParkingDetailsViewModel(private val parkingDAO: ParkingDAO) : ViewModel() {
    private val _parking = MutableStateFlow<Parking?>(null)
    val parking: StateFlow<Parking?> = _parking

    fun getParkingById(id: String) {
        viewModelScope.launch {
            _parking.value = parkingDAO.getParkingById(id)
        }
    }
}
