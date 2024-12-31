package com.example.taskhub.repository

import com.example.taskhub.dao.TaskDao
import com.example.taskhub.model.Task
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    suspend fun insertTask(task: Task){
        taskDao.insertTask(task)
    }

    suspend fun deleteTask(task: Task){
        taskDao.deleteTask(task)
    }

    suspend fun updateTask(task: Task){
        taskDao.updateTask(task)
    }

    fun getAllTasks() = taskDao.getAllTasks()

    fun getTasksByCategory(categoryId : Int) = taskDao.getTasksByCategory(categoryId)

    fun getTaskByTaskId(taskId : Int) = taskDao.getTaskByTaskId(taskId)

    fun getTotalTaskCount(): Flow<Int> = taskDao.getTotalTaskCount()

    fun getTaskCountForCategory(categoryId: Int): Flow<Int> =
        taskDao.getTaskCountForCategory(categoryId)
}