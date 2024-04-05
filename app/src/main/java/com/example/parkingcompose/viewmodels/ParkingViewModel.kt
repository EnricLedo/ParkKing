package com.example.parkingcompose.viewmodels



import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.example.parkingcompose.data.Parking

class ParkingViewModel : ViewModel() {
    private val parkingList = mutableListOf<Parking>()

    init {
        // Ejemplo de parking 1
        val parking1 = Parking(
            parkingId = 1,
            location = Pair(37.7749, -122.4194), // Ejemplo de coordenadas para San Francisco
            name = "Parking 1",
            description = "Este es un parking de ejemplo",
            image = ImageBitmap(100, 100), // Supongamos que tienes una imagen de tamaño 100x100
            parkingRating = 4.5f,
            reviewList = listOf(), // Lista de revisiones vacía
            tagList = listOf(), // Lista de etiquetas vacía
            priceMinute = 0.5f
        )
        parkingList.add(parking1)

        // Ejemplo de parking 2
        val parking2 = Parking(
            parkingId = 2,
            location = Pair(40.7128, -74.0060), // Ejemplo de coordenadas para Nueva York
            name = "Parking 2",
            description = "Otro parking de ejemplo",
            image = ImageBitmap(100, 100), // Supongamos que tienes una imagen de tamaño 100x100
            parkingRating = 4.0f,
            reviewList = listOf(), // Lista de revisiones vacía
            tagList = listOf(), // Lista de etiquetas vacía
            priceMinute = 0.75f
        )
        parkingList.add(parking2)
    }

    fun getParkingList(): MutableList<Parking> {
        return parkingList
    }
}