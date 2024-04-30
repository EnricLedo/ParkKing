package com.example.parkingcompose.viewmodels

import android.content.ContentValues.TAG
import android.location.Location
import android.location.Location.distanceBetween
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.model.Parking
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ParkingViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _parkingList = MutableStateFlow<List<Parking>>(emptyList())
    val parkingList: StateFlow<List<Parking>> = _parkingList
    private val _selectedTags = MutableStateFlow<Set<String>>(setOf())
    private val _searchQuery = MutableStateFlow("")
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val userLocation = MutableStateFlow(com.example.parkingcompose.model.Location(0.0, 0.0)) // MutableStateFlow to track user location
    private val _filteredParkings = MutableStateFlow<List<Parking>>(emptyList())
    private val _selectedRating = MutableStateFlow<Int?>(null)
    private val _selectedDistance = MutableStateFlow<Float?>(null)
    val selectedDistance: StateFlow<Float?> = _selectedDistance

    val selectedRating: StateFlow<Int?> = _selectedRating
    val filteredParkings: StateFlow<List<Parking>> = combine(
        _parkingList, _selectedTags, _searchQuery, _selectedRating, _selectedDistance
    ) { parkings, selectedTags, query, selectedRating, selectedDistance ->
        parkings.filter { parking ->
            (selectedTags.isEmpty() || parking.tags.intersect(selectedTags).isNotEmpty()) &&
                    (query.isBlank() || parking.name.contains(query, ignoreCase = true)) &&
                    (selectedRating == null || parking.parkingRating.toInt() >= selectedRating) && // Cambio aquí
                    (selectedDistance == null || distanceBetween(userLocation.value, parking.location) <= selectedDistance)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())


    fun setSelectedDistance(distance: Float?) {
        _selectedDistance.value = distance
    }

    private fun distanceBetween(userLocation: com.example.parkingcompose.model.Location, parkingLocation: com.example.parkingcompose.model.Location): Float {
        val results = FloatArray(1)
        Location.distanceBetween(userLocation.latitude, userLocation.longitude, parkingLocation.latitude, parkingLocation.longitude, results)
        return results[0] / 1000
    }

    init {
        getParkingList()
    }

    fun setSelectedRating(rating: Int?) {
        _selectedRating.value = rating
    }
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }
    fun orderByCreationDate() {
        _parkingList.value = _parkingList.value.sortedByDescending { it.createdAt }
    }
    fun updateSelectedTags(selectedTags: Set<String>) {
        _selectedTags.value = selectedTags
    }
    fun resetSearch() {
        // Reinicia los filtros aquí...
        _selectedRating.value = null
        _selectedTags.value = emptySet()
        _searchQuery.value = ""
        _selectedDistance.value = null

        // Luego obtén la lista de parkings de nuevo
        getParkingList()
    }

    fun orderParkingsByBestRating() {
        viewModelScope.launch {
            val sortedParkings = _parkingList.value.sortedByDescending { it.parkingRating }
            _parkingList.value = sortedParkings
        }
    }

    fun orderParkingsByWorstRating() {
        viewModelScope.launch {
            val sortedParkings = _parkingList.value.sortedBy { it.parkingRating }
            _parkingList.value = sortedParkings
        }
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


    fun setUserLocation(lat: Double, lng: Double) {
        userLocation.value = com.example.parkingcompose.model.Location(lat, lng)
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

    fun filterParkingsByDistance() {
        val userLat = userLocation.value.latitude
        val userLng = userLocation.value.longitude
        val maxDistance = _selectedDistance.value

        viewModelScope.launch {
            val allParkings = db.collection("parkings").get().await().toObjects(Parking::class.java)
            if (maxDistance != null) {
                _parkingList.value = allParkings.filter { parking ->
                    val dist = FloatArray(1)
                    Location.distanceBetween(userLat, userLng, parking.location.latitude, parking.location.longitude, dist)
                    dist[0] / 1000 <= maxDistance
                }
            }
        }
    }

}
