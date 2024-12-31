package com.example.taskhub.repository

import com.example.taskhub.dao.CategoryDao
import com.example.taskhub.model.Category
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao : CategoryDao) {

    suspend fun insertCategory(category: Category){
        categoryDao.insertCategory(category)
    }

    suspend fun deleteCategory(category: Category){
        categoryDao.deleteCategory(category)
    }

    fun getAllCategories(): Flow<List<Category>> = categoryDao.getAllCategories()

    fun getCategoryById(id: Int): Flow<Category> {
        return categoryDao.getCategoryById(id)
    }
}