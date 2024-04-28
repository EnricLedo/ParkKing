package com.example.parkingcompose.model

import EditParkingViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.parkingcompose.dao.ParkingDAO
import com.example.parkingcompose.viewmodels.TagViewModel


class EditParkingViewModelFactory(private val parkingDAO: ParkingDAO, private val parkingId: String, private val tagViewModel: TagViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(EditParkingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return EditParkingViewModel(parkingDAO, parkingId, tagViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}