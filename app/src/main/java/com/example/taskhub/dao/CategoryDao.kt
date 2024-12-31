package com.example.taskhub.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.taskhub.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert()
    suspend fun insertCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("select * from category")
     fun getAllCategories() : Flow<List<Category>>

    @Query("select * from category where catId= :categoryId")
    fun getCategoryById(categoryId: Int) : Flow<Category>
}