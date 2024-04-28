package com.example.parkingcompose.model

import EditParkingViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.parkingcompose.dao.ParkingDAO


class EditParkingViewModelFactory(private val parkingDAO: ParkingDAO, private val parkingId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditParkingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditParkingViewModel(parkingDAO, parkingId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}