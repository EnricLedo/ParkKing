package com.example.parkingcompose.dao


import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class ParkingDAO {
    private val db = FirebaseFirestore.getInstance()

    fun enableParking(id: String): Task<Void> {
        return db.collection("parkings")
            .document(id)
            .update("checked", true)
    }

    companion object {
        private const val TAG = "ParkingDao"
    }
}