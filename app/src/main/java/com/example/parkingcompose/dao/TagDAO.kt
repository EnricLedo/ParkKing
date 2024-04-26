package com.example.parkingcompose.dao

import com.example.parkingcompose.model.Tag
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.util.UUID

class TagDAO {
    private val firestore = FirebaseFirestore.getInstance()
    private val storageReference = FirebaseStorage.getInstance().reference

    suspend fun getTagByTitle(title: String): Tag? {
        val querySnapshot = firestore.collection("tags").whereEqualTo("title", title).get().await()
        return querySnapshot.documents.mapNotNull { it.toObject(Tag::class.java) }.firstOrNull()
    }
    fun addTag(tag: Tag, onSuccess: () -> Unit, onTagExists: () -> Unit) {
        firestore.collection("tags")
            .whereEqualTo("title", tag.title)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (querySnapshot.isEmpty) {
                    firestore.collection("tags").add(tag)
                        .addOnSuccessListener {
                            onSuccess()
                        }
                        .addOnFailureListener { e ->
                            // handle error
                        }
                } else {
                    onTagExists()
                }
            }
            .addOnFailureListener { e ->
                // handle error
            }
    }

    fun updateTag(tag: Tag, onSuccess: () -> Unit, onFailure: () -> Unit) {
    // Comprueba si el nombre del tag está disponible
    firestore.collection("tags")
        .whereEqualTo("title", tag.title)
        .get()
        .addOnSuccessListener { documents ->
            if (documents.isEmpty) {
                // Si el nombre del tag está disponible, actualiza el tag
                firestore.collection("tags").document(tag.title).set(tag)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onFailure()
                    }
            } else {
                // Si el nombre del tag no está disponible, llama a la función de error
                onFailure()
            }
        }
        .addOnFailureListener {
            onFailure()
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

    suspend fun getTag(title: String): Tag? {
        return withContext(Dispatchers.IO) {
            firestore.collection("tags").document(title).get().await().toObject(Tag::class.java)
        }
    }
}