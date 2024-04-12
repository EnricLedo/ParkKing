package com.example.parkingcompose.viewmodels

import android.content.ContentValues.TAG
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parkingcompose.data.Location
import com.example.parkingcompose.data.Parking
import com.example.parkingcompose.util.StorageUtil
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class CreateParkingViewModel : ViewModel() {
    var name = mutableStateOf("")
    var description = mutableStateOf("")
    var priceMinute = mutableStateOf("")
    var latitude = mutableStateOf(0.0)
    var longitude = mutableStateOf(0.0)
    var selectedImage = mutableStateOf<Uri?>(null)

    private val _parkingAddedEvent = MutableSharedFlow<Unit>()
    val parkingAddedEvent: SharedFlow<Unit> = _parkingAddedEvent

    fun onNameChange(newValue: String) {
        name.value = newValue
    }

    fun onDescriptionChange(newValue: String) {
        description.value = newValue
    }

    fun onPriceMinuteChange(newValue: String) {
        priceMinute.value = newValue
    }

    suspend fun onAddParking(context: Context, selectLocationViewModel: SelectLocationViewModel) {
        val imageUrl = StorageUtil.uploadImageToFirebaseStorage(selectedImage.value)

        // Comprueba si imageUrl es null
        if (imageUrl != null) {
            // Obtiene la ubicación seleccionada
            val selectedLocation = selectLocationViewModel.selectedLocation.value

            if (selectedLocation != null) {
                val price = if (priceMinute.value.isNotEmpty()) {
                    priceMinute.value.toFloat()
                } else {
                    // Set a default value or show an error message
                    Toast.makeText(context, "Please enter a price -> OnAddParking Method", Toast.LENGTH_SHORT).show()
                    return
                }

                val parking = Parking(
                    location = Location(selectedLocation.latitude, selectedLocation.longitude),
                    name = name.value,
                    description = description.value,
                    image = imageUrl, // Usa la URL obtenida
                    parkingRating = 0.0f,
                    reviewList = emptyList(),
                    tagList = emptyList(),
                    priceMinute = price
                )
                addParking(parking, context)
            } else {
                Toast.makeText(context, "Please select a location -> OnAddParking Method", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Failed to upload image -> OnAddParking Method", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addParking(parking: Parking, localContext: Context) {
        val db = FirebaseFirestore.getInstance()

        // Añade un nuevo documento a la colección "parkings"
        db.collection("parkings")
            .add(parking)
            .addOnSuccessListener { documentReference ->
                Log.d(TAG, "Parking added with ID: ${documentReference.id}")
                viewModelScope.launch {
                    _parkingAddedEvent.emit(Unit) // Emit an update event
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding parking", e)
                Toast.makeText (localContext, "Error adding parking -> addParking Method", Toast.LENGTH_SHORT).show()
            }
    }
}