package com.example.parkingcompose.util

import android.net.Uri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.Dispatchers
import java.util.UUID
import kotlinx.coroutines.withContext

class StorageUtil {


    companion object {
        suspend fun uploadImageToFirebaseStorage(imageUri: Uri?): String? {
            // Comprueba que la Uri no sea null
            if (imageUri == null) {
                return null
            }

            val storage = Firebase.storage
            val storageRef = storage.reference.child("images/${UUID.randomUUID()}.jpg")

            return withContext(Dispatchers.IO) {
                val uploadTask = storageRef.putFile(imageUri)

                // Espera a que la imagen se suba y obtén la URL
                try {
                    Tasks.await(uploadTask)
                    val downloadUrl = Tasks.await(storageRef.downloadUrl)
                    downloadUrl.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            }
        }
    }
}