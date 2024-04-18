package com.example.parkingcompose.util

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.parkingcompose.dao.ParkingDAO
import com.example.parkingcompose.viewmodels.ParkingDetailsViewModel

class ParkingDetailsViewModelFactory(private val parkingDAO: ParkingDAO) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ParkingDetailsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ParkingDetailsViewModel(parkingDAO) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}