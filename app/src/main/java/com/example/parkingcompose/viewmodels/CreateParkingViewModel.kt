package com.example.parkingcompose.viewmodels

import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.example.parkingcompose.ParkingListActivity
import com.example.parkingcompose.data.Parking
import com.example.parkingcompose.util.StorageUtil
import com.google.firebase.firestore.FirebaseFirestore


class CreateParkingViewModel : ViewModel() {
    var name = mutableStateOf("")
    var description = mutableStateOf("")
    var priceMinute = mutableStateOf("")

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
            val parking = Parking(
                parkingId = 0,
                location = Pair(0.0, 0.0),
                name = name.value,
                description = description.value,
                image = imageUrl, // Usa la URL obtenida
                parkingRating = 0.0f,
                reviewList = emptyList(),
                tagList = emptyList(),
                priceMinute = priceMinute.value.toFloat()
            )
            addParking(parking, context)
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
                val intent = Intent(localContext, ParkingListActivity::class.java)
                ContextCompat.startActivity(localContext, intent, null)
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error adding parking", e)
                Toast.makeText (localContext, "Error adding parking -> addParking Method", Toast.LENGTH_SHORT).show()
            }
    }
}