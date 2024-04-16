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

    private val _updateEvent = MutableSharedFlow<Unit>()
    val updateEvent: SharedFlow<Unit> = _updateEvent

    fun onNameChange(newValue: String) {
        name.value = newValue
    }

    fun onDescriptionChange(newValue: String) {
        description.value = newValue
    }

    fun onPriceMinuteChange(newValue: String) {
        priceMinute.value = newValue
    }

    suspend fun onAddParking(context: Context, image: Uri?) {
        val imageUrl = StorageUtil.uploadImageToFirebaseStorage(image)

        // Comprueba si imageUrl es null
        if (imageUrl != null) {
            val db = FirebaseFirestore.getInstance()

            // Crea un nuevo documento en la colección "parkings" con un ID automático
            val newParkingRef = db.collection("parkings").document()

            val parking = Parking(
                id = newParkingRef.id, // Usa el ID del nuevo documento
                location = Location(0.0, 0.0),
                name = name.value,
                description = description.value,
                image = imageUrl, // Usa la URL obtenida
                parkingRating = 0.0f,
                reviewList = emptyList(),
                tagList = emptyList(),
                priceMinute = priceMinute.value.toFloat()
            )

            // Sube el objeto Parking a Firestore
            newParkingRef.set(parking)
                .addOnSuccessListener {
                    Log.d(TAG, "Parking added with ID: ${newParkingRef.id}")
                    viewModelScope.launch {
                        _updateEvent.emit(Unit) // Emit an update event
                    }
                }
                .addOnFailureListener { e ->
                    Log.w(TAG, "Error adding parking", e)
                    Toast.makeText(context, "Error adding parking -> addParking Method", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(context, "Failed to upload image -> OnAddParking Method", Toast.LENGTH_SHORT).show()
        }
    }
}