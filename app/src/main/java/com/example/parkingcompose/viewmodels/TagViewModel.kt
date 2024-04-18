package com.example.parkingcompose.viewmodels


import android.util.Log
import com.example.parkingcompose.model.Tag
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class TagViewModel {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun addTag(tag: Tag) {
        try {
            firestore.collection("tags").add(tag).await()
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error adding tag", e)
            // Considera mostrar un mensaje de error al usuario.
        }
    }

    suspend fun updateTag(tag: Tag) {
        val tagRef = tag.id?.let { firestore.collection("tags").document(it) }
        tagRef?.set(tag)?.await()
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
}
