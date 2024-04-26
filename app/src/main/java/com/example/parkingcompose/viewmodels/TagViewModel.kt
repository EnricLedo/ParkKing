package com.example.parkingcompose.viewmodels


import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.parkingcompose.dao.TagDAO
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
    private val tagDAO = TagDAO()
    private val _selectedTags = MutableStateFlow<Set<String>>(emptySet())
    val selectedTags: StateFlow<Set<String>> = _selectedTags

    fun addTag(tag: Tag, onSuccess: () -> Unit, onTagExists: () -> Unit) {
        tagDAO.addTag(tag, onSuccess, onTagExists)
    }

    fun updateTag(tag: Tag, onSuccess: () -> Unit, onTagExists: () -> Unit) {
        tagDAO.updateTag(tag, onSuccess, onTagExists)
    }

    suspend fun deleteTag(tagId: String) {
        tagDAO.deleteTag(tagId)
    }

    fun getTagsFlow(): Flow<List<Tag>> {
        return tagDAO.getTagsFlow()
    }

    fun uploadImageToFirebaseStorage(inputStream: InputStream, onSuccess: (String) -> Unit, onFailure: () -> Unit) {
        tagDAO.uploadImageToFirebaseStorage(inputStream, onSuccess, onFailure)
    }

    suspend fun getTagsById(tagId: String): Tag {
        return tagDAO.getTagsById(tagId)
    }

    suspend fun getTag(title: String): Tag? {
        return tagDAO.getTag(title)
    }
}
