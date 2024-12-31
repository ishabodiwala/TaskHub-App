package com.example.taskhub.depedencyInjection

import android.content.Context
import com.example.taskhub.dao.CategoryDao
import com.example.taskhub.dao.TaskDao
import com.example.taskhub.database.ToDoDataBase
import com.example.taskhub.repository.CategoryRepository
import com.example.taskhub.repository.TaskRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ToDoDataBase {
        return ToDoDataBase.getDatabase(context)
    }

    @Provides
    fun provideCategoryDao(database: ToDoDataBase): CategoryDao = database.categoryDao()

    @Provides
    fun provideTaskDao(database: ToDoDataBase): TaskDao = database.taskDao()

    @Provides
    fun provideCategoryRepository(categoryDao: CategoryDao): CategoryRepository =
        CategoryRepository(categoryDao)

    @Provides
    fun provideTaskRepository(taskDao: TaskDao): TaskRepository =
        TaskRepository(taskDao)
}
