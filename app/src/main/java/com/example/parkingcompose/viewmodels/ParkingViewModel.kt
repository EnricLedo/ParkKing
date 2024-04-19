package com.example.parkingcompose.viewmodels

import android.content.ContentValues.TAG
import android.location.Location
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.data.Parking
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class ParkingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _parkingList = MutableStateFlow<List<Parking>>(emptyList())
    private val _selectedTags = MutableStateFlow<Set<String>>(setOf())
    private val _error = MutableStateFlow<String?>(null)
    private val _searchQuery = MutableStateFlow("")
    val error: StateFlow<String?> = _error
    private val _filteredParkings = MutableStateFlow<List<Parking>>(emptyList())
    val filteredParkings: StateFlow<List<Parking>> = combine(
        _parkingList, _selectedTags, _searchQuery
    ) { parkings, selectedTags, query ->
        parkings.filter { parking ->
            (selectedTags.isEmpty() || parking.tags.intersect(selectedTags).isNotEmpty()) &&
                    (query.isBlank() || parking.name.contains(query, ignoreCase = true))
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())
    init {
        getParkingList()
    }
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    fun orderByCreationDate() {
        _parkingList.value = _parkingList.value.sortedByDescending { it.createdAt }
    }


    fun updateSelectedTags(tagId: String) {
        _selectedTags.value = if (_selectedTags.value.contains(tagId)) {
            _selectedTags.value - tagId
        } else {
            _selectedTags.value + tagId
        }
        Log.d(TAG, "Selected tags updated: ${_selectedTags.value}")
    }




    fun getParkingList() {
        viewModelScope.launch {
            try {
                val querySnapshot = db.collection("parkings").get().await()
                val parkings = querySnapshot.documents.mapNotNull {
                    it.toObject(Parking::class.java)?.apply {
                        // Ensure tags are properly parsed and other necessary fields
                    }
                }
                _parkingList.value = parkings
            } catch (e: Exception) {
                _error.value = "Failed to load parkings: ${e.localizedMessage}"
            }
        }
    }

    fun filterParkings(userLat: Double, userLng: Double, minDist: Float, maxDist: Float) {
        viewModelScope.launch {
            val allParkings = db.collection("parkings").get().await().toObjects(Parking::class.java)
            _filteredParkings.value = allParkings.filter {
                val dist = FloatArray(1)
                Location.distanceBetween(userLat, userLng, it.location.latitude, it.location.longitude, dist)
                dist[0] in minDist..maxDist
            }
        }
    }


    private val userLocation = MutableStateFlow(com.example.parkingcompose.data.Location(0.0, 0.0)) // MutableStateFlow to track user location

    fun setUserLocation(lat: Double, lng: Double) {
        userLocation.value = com.example.parkingcompose.data.Location(lat, lng)
        orderParkingsByDistance() // Orden inicial por distancia más corta
    }

    fun orderParkingsByDistance(ascending: Boolean = true) {
        viewModelScope.launch {
            val allParkings = db.collection("parkings").get().await().toObjects(Parking::class.java)
            _parkingList.value = allParkings.sortedBy { parking ->
                val dist = FloatArray(1)
                android.location.Location.distanceBetween(userLocation.value.latitude, userLocation.value.longitude, parking.location.latitude, parking.location.longitude, dist)
                dist[0]
            }
            if (!ascending) {
                _parkingList.value = _parkingList.value.reversed()
            }
        }
    }


}
