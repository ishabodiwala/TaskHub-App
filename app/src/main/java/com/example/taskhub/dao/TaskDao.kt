package com.example.taskhub.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.taskhub.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Insert
    suspend fun insertTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

    @Update
    suspend fun updateTask(task: Task)

    @Query("select * from tasks")
    fun getAllTasks() : Flow<List<Task>>

    @Query("select * from tasks WHERE categoryId = :categoryId")
    fun getTasksByCategory(categoryId: Int): Flow<List<Task>>

    @Query("select * from tasks WHERE id = :taskId")
    fun getTaskByTaskId(taskId: Int): Flow<Task>

    @Query("SELECT COUNT(*) FROM tasks")
    fun getTotalTaskCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM tasks WHERE categoryId = :categoryId")
    fun getTaskCountForCategory(categoryId: Int): Flow<Int>
}