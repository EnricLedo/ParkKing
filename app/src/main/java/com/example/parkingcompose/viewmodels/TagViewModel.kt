package com.example.parkingcompose.viewmodels


import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.parkingcompose.model.Tag
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.InputStream
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import java.util.UUID


class TagViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference
    private val _selectedTags = MutableStateFlow<Set<String>>(emptySet())
    val selectedTags: StateFlow<Set<String>> = _selectedTags

    fun addTag(tag: Tag, onSuccess: () -> Unit, onTagExists: () -> Unit) {
        // Primero verifica si ya existe un tag con el mismo título
        firestore.collection("tags")
            .whereEqualTo("title", tag.title)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    // Si no existe ningún tag con ese título, procede a añadir el nuevo tag
                    firestore.collection("tags").add(tag)
                        .addOnSuccessListener {
                            Log.d(TAG, "Tag added successfully with ID: ${it.id}")
                            onSuccess()  // Llama al callback de éxito
                        }
                        .addOnFailureListener { e ->
                            Log.w(TAG, "Error adding tag", e)
                        }
                } else {
                    // Si ya existe un tag con ese título, llama al callback de error
                    onTagExists()
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Error checking for existing tags", e)
            }
    }


    fun updateTag(tag: Tag, onSuccess: () -> Unit, onTagExists: () -> Unit) {
        // Verifica primero si ya existe un tag con el mismo título y diferente ID
        firestore.collection("tags")
            .whereEqualTo("title", tag.title)
            .get()
            .addOnSuccessListener { documents ->
                if (documents.any { it.id != tag.id }) {
                    onTagExists()
                } else {
                    // No hay conflictos, procede a actualizar
                    tag.id?.let { id ->
                        firestore.collection("tags").document(id).set(tag)
                            .addOnSuccessListener {
                                onSuccess()
                            }
                            .addOnFailureListener {
                                Log.w(TAG, "Error updating tag", it)
                            }
                    }
                }
            }
            .addOnFailureListener {
                Log.w(TAG, "Error checking for existing tags", it)
            }
    }



    suspend fun deleteTag(tagId: String) {
        val tagRef = firestore.collection("tags").document(tagId)
        tagRef.delete().await()
    }

    fun getTagsFlow(): Flow<List<Tag>> = callbackFlow {
        val tagsRef = firestore.collection("tags").orderBy("title")

        val subscription = tagsRef.addSnapshotListener { snapshot, _ ->
            snapshot?.let { querySnapshot ->
                val tags = mutableListOf<Tag>()
                for (document in querySnapshot.documents) {
                    val tag = document.toObject(Tag::class.java)
                    tag?.id = document.id
                    tag?.let { tags.add(it) }
                }
                trySend(tags).isSuccess
            }
        }
        awaitClose { subscription.remove() }
    }

    fun uploadImageToFirebaseStorage(inputStream: InputStream, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        val ref = storageReference.child("images/${UUID.randomUUID()}")
        val uploadTask = ref.putStream(inputStream)
        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                onSuccess(task.result.toString())
            } else {
                onFailure()
            }
        }
    }

    suspend fun getTagsById(tagId: String): Tag {
        return withContext(Dispatchers.IO) {
            firestore.collection("tags").document(tagId).get().await().toObject(Tag::class.java) ?: Tag()
        }
    }

}
