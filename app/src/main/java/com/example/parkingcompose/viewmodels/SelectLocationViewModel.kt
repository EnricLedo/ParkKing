package com.example.parkingcompose.viewmodels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SelectLocationViewModel : ViewModel() {
    // MutableStateFlow para almacenar la ubicación seleccionada
    private val _selectedLocation = MutableStateFlow<LatLng?>(null)
    val selectedLocation: StateFlow<LatLng?> get() = _selectedLocation

    // Función para actualizar la ubicación seleccionada
    fun updateSelectedLocation(location: LatLng) {
        viewModelScope.launch {
            _selectedLocation.emit(location)
        }
    }
    fun resetSelectedLocation() {
        viewModelScope.launch {
            _selectedLocation.emit(null)
        }
    }

}