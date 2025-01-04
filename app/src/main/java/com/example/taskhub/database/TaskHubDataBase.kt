package com.example.taskhub.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.taskhub.dao.CategoryDao
import com.example.taskhub.dao.TaskDao
import com.example.taskhub.model.Category
import com.example.taskhub.model.Task

@Database(entities = [Task::class, Category::class], version = 1)
abstract class TaskHubDataBase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
    abstract fun categoryDao(): CategoryDao

    companion object {
        @Volatile
        private var INSTANCE: TaskHubDataBase? = null
        fun getDatabase(context: Context): TaskHubDataBase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskHubDataBase::class.java,
                    "taskHub_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}