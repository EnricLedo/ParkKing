package com.copernic.crud2

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.copernic.crud2.Category.AppDatabase
import com.copernic.crud2.Category.Category
import com.copernic.crud2.Category.CategoryDao
import kotlinx.coroutines.launch

class CategoryViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CategoryRepository
    val allCategories: LiveData<List<Category>>
        private val _selectedImageUri = MutableLiveData<Uri?>()
        val selectedImageUri: LiveData<Uri?> = _selectedImageUri

        fun setSelectedImageUri(uri: Uri?) {
            _selectedImageUri.value = uri
        }

    init {
        val categoryDao = AppDatabase.getDatabase(application).categoryDao()
        repository = CategoryRepository(categoryDao)
        allCategories = repository.allCategories
    }

    fun insert(name: String, description: String, imageUri: String?) = viewModelScope.launch {
        val newCategory = Category(name = name, description = description, icon = imageUri ?: "")
        repository.insert(newCategory)
    }

    fun update(category: Category) = viewModelScope.launch {
        repository.update(category)
    }

    fun delete(category: Category) = viewModelScope.launch {
        repository.delete(category)
    }

}

class CategoryRepository(private val categoryDao: CategoryDao) {
    val allCategories: LiveData<List<Category>> = categoryDao.getAll()

    suspend fun insert(category: Category) {
        categoryDao.insert(category)
    }
    suspend fun update(category: Category) {
        categoryDao.update(category)
    }
    suspend fun delete(category: Category) {
        categoryDao.delete(category)
    }

}
